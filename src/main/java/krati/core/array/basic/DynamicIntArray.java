/*
 * Copyright (c) 2010-2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package krati.core.array.basic;

import java.io.File;
import java.io.IOException;

import krati.array.Array;
import krati.array.DynamicArray;
import krati.array.IntArray;
import krati.core.array.entry.EntryIntFactory;
import krati.core.array.entry.EntryValueInt;

import org.apache.log4j.Logger;

/**
 * DynamicIntArray
 * 
 * @author jwu
 *
 */
public class DynamicIntArray extends AbstractRecoverableArray<EntryValueInt> implements IntArray, DynamicArray, ArrayExpandListener {
    private final static int _subArrayBits = DynamicConstants.SUB_ARRAY_BITS;
    private final static int _subArraySize = DynamicConstants.SUB_ARRAY_SIZE;
    private final static Logger _log = Logger.getLogger(DynamicIntArray.class);
    private MemoryIntArray _internalArray;
    
    public DynamicIntArray(int entrySize, int maxEntries, File directory) throws Exception {
        super(_subArraySize /* initial array length and subArray length */, 4, entrySize, maxEntries, directory, new EntryIntFactory());
    }
    
    @Override
    protected Logger getLogger() {
        return _log;
    }
    
    @Override
    protected void loadArrayFileData() throws IOException {
        long maxScn = _arrayFile.getLwmScn();
        
        try {
            _internalArray = new MemoryIntArray(_subArrayBits);
            _arrayFile.load(_internalArray);
            
            expandCapacity(_internalArray.length() - 1);
            _internalArray.setArrayExpandListener(this);
        } catch(Exception e) {
            throw (e instanceof IOException) ? (IOException)e : new IOException("Failed to load array file", e);
        }
        
        _entryManager.setWaterMarks(maxScn, maxScn);
    }
    
    /**
     * Sync-up the high water mark to a given value.
     * 
     * @param endOfPeriod
     */
    @Override
    public void saveHWMark(long endOfPeriod) {
        if (getHWMark() < endOfPeriod) {
            try {
                set(0, get(0), endOfPeriod);
            } catch(Exception e) {
                _log.error("Failed to saveHWMark " + endOfPeriod, e);
            }
        } else if(0 < endOfPeriod && endOfPeriod < getLWMark()) {
            try {
                _entryManager.sync();
            } catch(Exception e) {
                _log.error("Failed to saveHWMark" + endOfPeriod, e);
            }
            _entryManager.setWaterMarks(endOfPeriod, endOfPeriod);
        }
    }
    
    @Override
    public void clear() {
        if (_internalArray != null) {
            _internalArray.clear();
        }
        
        // Clear the entry manager
        _entryManager.clear();
        
        // Clear the underlying array file
        try {
            _arrayFile.reset(_internalArray, _entryManager.getLWMark());
        } catch(IOException e) {
            _log.error(e.getMessage(), e);
        }
    }
    
    @Override
    public int get(int index) {
        return _internalArray.get(index);
    }
    
    @Override
    public void set(int index, int value, long scn) throws Exception {
        _internalArray.set(index, value);
        _entryManager.addToPreFillEntryInt(index, value, scn);
    }
    
    @Override
    public int[] getInternalArray() {
        return _internalArray.getInternalArray();
    }

    @Override
    public void expandCapacity(int index) throws Exception {
        if(index < _length) return;
        
        long capacity = ((index >> _subArrayBits) + 1L) * _subArraySize;
        int newLength = (capacity < Integer.MAX_VALUE) ? (int)capacity : Integer.MAX_VALUE;
        
        // Expand internal array in memory
        if(_internalArray.length() < newLength) {
            _internalArray.expandCapacity(index);
        }
        
        // Expand array file on disk
        _arrayFile.setArrayLength(newLength, null /* do not rename */);
        
        // Reset _length
        _length = newLength;
        
        // Add to logging
        _log.info("Expanded: _length=" + _length);
    }
    
    @Override
    public void arrayExpanded(DynamicArray dynArray) {
        if(dynArray == _internalArray) {
            try {
                expandCapacity(dynArray.length() - 1);
            } catch(Exception e) {
                _log.error("Failed to expand: length=" + dynArray.length());
            }
        }
    }
    
    public final int subArrayLength() {
        return _subArraySize;
    }
    
    @Override
    public final Array.Type getType() {
        return Array.Type.DYNAMIC;
    }
}
