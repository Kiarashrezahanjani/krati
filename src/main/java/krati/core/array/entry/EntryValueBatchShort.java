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

package krati.core.array.entry;

/**
 * EntryValueBatchShort
 * 
 * @author jwu
 * 
 */
public class EntryValueBatchShort extends EntryValueBatch {
    public EntryValueBatchShort() {
        this(1000);
    }
    
    public EntryValueBatchShort(int capacity) {
        /*
         * EntryValueLong int position; short value; long scn;
         */
        super(14, capacity);
    }
    
    public void add(int pos, short val, long scn) {
        _buffer.putInt(pos);   /* array position */
        _buffer.putShort(val); /* data value */
        _buffer.putLong(scn);  /* SCN value */
    }
}
