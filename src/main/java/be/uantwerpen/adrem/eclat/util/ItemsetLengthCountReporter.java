/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.uantwerpen.adrem.eclat.util;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableLong;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * Implementation of a Set Reporter that writes counts for different itemset levels (i.e., itemset lenghts) to the
 * output.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemsetLengthCountReporter implements SetReporter {
  
  private final Context context;
  
  Map<Integer,MutableLong> counts = newHashMap();
  
  public ItemsetLengthCountReporter(Context context) {
    this.context = context;
  }
  
  @Override
  public void report(int[] itemset, int support) {
    int size = itemset.length;
    MutableLong count = counts.get(size);
    if (count == null) {
      count = new MutableLong();
      counts.put(size, count);
    }
    count.increment();
  }
  
  @Override
  public void close() {
    try {
      for (Entry<Integer,MutableLong> entry : counts.entrySet()) {
        context.write(new Text("" + entry.getKey()), new LongWritable(entry.getValue().longValue()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}