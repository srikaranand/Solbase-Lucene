package org.apache.lucene.search;

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

import java.io.IOException;

/**
 * A {@link Scorer} which wraps another scorer and caches the score of the
 * current document. Successive calls to {@link #score()} will return the same
 * result and will not invoke the wrapped Scorer's score() method, unless the
 * current document has changed.<br>
 * This class might be useful due to the changes done to the {@link Collector}
 * interface, in which the score is not computed for a document by default, only
 * if the collector requests it. Some collectors may need to use the score in
 * several places, however all they have in hand is a {@link Scorer} object, and
 * might end up computing the score of a document more than once.
 */
public class ScoreCachingWrappingScorer extends Scorer {

  private Scorer scorer;
  private int curDoc = -1;
  private float curScore;
  private int[] curSorts = new int[Scorer.numSort];
  
  /** Creates a new instance by wrapping the given scorer. */
  public ScoreCachingWrappingScorer(Scorer scorer) {
    super(scorer.getSimilarity());
    this.scorer = scorer;
  }

  protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
    return scorer.score(collector, max, firstDocID);
  }

  public Similarity getSimilarity() {
    return scorer.getSimilarity();
  }
  
  public Explanation explain(int doc) throws IOException {
    return scorer.explain(doc);
  }

  public float score() throws IOException {
    int doc = scorer.docID();
    if (doc != curDoc) {
      curScore = scorer.score();
      curDoc = doc;
      copySorts(scorer.getSorts(), this.curSorts);
    }
    
    return curScore;
  }

  /** @deprecated use {@link #docID()} instead. */
  public int doc() {
    return scorer.doc();
  }
  
  public int docID() {
    return scorer.docID();
  }

  /** @deprecated use {@link #nextDoc()} instead. */
  public boolean next() throws IOException {
    return scorer.next();
  }

  public int nextDoc() throws IOException {
    return scorer.nextDoc();
  }
  
  public void score(Collector collector) throws IOException {
    scorer.score(collector);
  }
  
  /** @deprecated use {@link #advance(int)} instead. */
  public boolean skipTo(int target) throws IOException {
    return scorer.skipTo(target);
  }

  public int advance(int target) throws IOException {
    return scorer.advance(target);
  }

public int getSort(int fieldNumber) {
	return this.curSorts[fieldNumber];
}

public int[] getSorts() {
	return this.curSorts;
}
  
}
