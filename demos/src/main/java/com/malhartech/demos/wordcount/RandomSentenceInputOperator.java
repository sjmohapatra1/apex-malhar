/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.demos.wordcount;

import com.malhartech.api.Context.OperatorContext;
import com.malhartech.api.DefaultOutputPort;
import com.malhartech.api.InputOperator;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Zhongjian Wang <zhongjian@malhar-inc.com>
 */
public class RandomSentenceInputOperator implements InputOperator
{
  private static final Logger logger = LoggerFactory.getLogger(RandomSentenceInputOperator.class);
  public transient DefaultOutputPort<String> output = new DefaultOutputPort<String>(this);
//  public static final String filename = "/home/wzj/Downloads/hadoop-1.0.4/terafile-10M";
  public static final String filename = "src/main/resources/com/malhartech/demos/wordcount/samplefile.txt";
  private BufferedReader br;
  private transient DataInputStream in;
  private final int batchSize = 1000;
  private transient String sentence;

  @Override
  public void emitTuples()
  {
    try {
      String line;
      int i = 0;
      while ((line = br.readLine()) != null) {
        String[] words = line.trim().split("[\\p{Punct}\\s\\\"\\'“”]+");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                //System.out.println("emitting "+word.toLowerCase());
                output.emit(word.toLowerCase());
            }
        }
        if (++i == batchSize) {
          break;
        }
      }
      if( line == null ) {
        throw new RuntimeException(new InterruptedException("No more tuples to emit!"));
      }
    }
    catch (IOException ex) {
      logger.debug(ex.toString());
    }
  }

  @Override
  public void beginWindow(long windowId)
  {
  }

  @Override
  public void endWindow()
  {
  }

  @Override
  public void setup(OperatorContext context)
  {
    try {
      if (br != null) {
        return;
      }
      FileInputStream fstream = new FileInputStream(filename);
      in = new DataInputStream(fstream);
      br = new BufferedReader(new InputStreamReader(in));
    }
    catch (FileNotFoundException ex) {
      logger.debug(ex.toString());
      throw new RuntimeException(new InterruptedException("tera data file is not ready!"));
    }
  }

  @Override
  public void teardown()
  {
    try {
      in.close();
    }
    catch (IOException ex) {
      logger.debug(ex.toString());
    }
  }
}
