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

import java.io.IOException;

/**
 * Listener for persist events.
 * 
 * @author jwu
 * 
 */
public interface EntryPersistListener {
    
    public void beforePersist(Entry<? extends EntryValue> e) throws IOException;
    
    public void afterPersist(Entry<? extends EntryValue> e) throws IOException;
}
