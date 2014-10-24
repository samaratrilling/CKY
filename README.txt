CKY
===
Time taken to run CKY on question 5 data without improvement:
377751661116 nanoseconds = 6.29 minutes
Time taken to run CKY on question 5 data with improvement:
353642475340 nanoseconds = 5.89 minutes

Time taken to run CKY on question 6 data with improvement:
2466347300610 ns (41 minutes)

RUN INSTRUCTION
----
EASY VERSION:
Just run run.bash. :)

It will run q4 on normal training data;
then q4 on verticalized training data;
then q5 on normal data
then q5 on verticalized data
then q6 on normal data
then q6 on verticalized data.

--------
HARD VERSION:
FOR QUESTION 4:

1. From inside the Q4/ directory:
Run q4.bash.

FOR QUESTION 5:
1. From inside the Q5/ directory:
Run q5.bash.

FOR QUESTION 6:
1. From inside the Q6/ directory:
Run q6.bash.
----------------------------------------------------
DESIGN:
For question 6, my optimization was to lower the number of nonterminals
that the algorithm has to consider; it only calculates pi(i, j, X)
if both pi(i, s, Y) and (s+1, j, Z) are nonzero.

On the standard non-verticalized training data, the runtime is approximately
40 seconds faster with the optimized version.



PERFORMANCE for algorithm (precision, recall, F-score) and OBSERVATIONS
----
Q4:
No relevant performance metrics. See replacedRare.dat, replaced.counts,
replacedRareVert.dat and replacedVert.counts for evidence of performance.

----
Q5:
   Type       Total   Precision      Recall     F1 Score
===============================================================
         .         370     1.000        1.000        1.000
       ADJ         110     0.555        0.827        0.664
      ADJP          21     0.241        0.333        0.280
  ADJP+ADJ          24     0.591        0.542        0.565
       ADP         202     0.946        0.955        0.951
       ADV          49     0.531        0.694        0.602
      ADVP          12     0.133        0.333        0.190
  ADVP+ADV          45     0.642        0.756        0.694
      CONJ          53     1.000        1.000        1.000
       DET         165     0.976        0.988        0.982
      NOUN         751     0.842        0.752        0.795
        NP         741     0.529        0.632        0.576
    NP+ADJ           7     1.000        0.286        0.444
    NP+DET          23     0.857        0.783        0.818
   NP+NOUN         117     0.573        0.641        0.605
    NP+NUM          14     0.231        0.214        0.222
   NP+PRON          50     0.980        0.980        0.980
     NP+QP           3     0.182        0.667        0.286
       NUM          61     0.645        0.984        0.779
        PP         221     0.625        0.588        0.606
      PRON          13     0.929        1.000        0.963
       PRT          46     0.978        0.957        0.967
   PRT+PRT           5     1.000        0.400        0.571
        QP          17     0.423        0.647        0.512
         S         733     0.782        0.626        0.695
      SBAR          11     0.040        0.091        0.056
      VERB         331     0.799        0.683        0.736
        VP         424     0.594        0.559        0.576
   VP+VERB          16     0.267        0.250        0.258

     total        4664     0.714        0.714        0.714

----
Q6:
      Type       Total   Precision      Recall     F1 Score
===============================================================
         .         370     1.000        1.000        1.000
       ADJ         164     0.827        0.555        0.664
      ADJP          29     0.333        0.241        0.280
  ADJP+ADJ          22     0.542        0.591        0.565
       ADP         204     0.955        0.946        0.951
       ADV          64     0.694        0.531        0.602
      ADVP          30     0.333        0.133        0.190
  ADVP+ADV          53     0.756        0.642        0.694
      CONJ          53     1.000        1.000        1.000
       DET         167     0.988        0.976        0.982
      NOUN         671     0.752        0.842        0.795
        NP         884     0.632        0.529        0.576
    NP+ADJ           2     0.286        1.000        0.444
    NP+DET          21     0.783        0.857        0.818
   NP+NOUN         131     0.641        0.573        0.605
    NP+NUM          13     0.214        0.231        0.222
   NP+PRON          50     0.980        0.980        0.980
     NP+QP          11     0.667        0.182        0.286
       NUM          93     0.984        0.645        0.779
        PP         208     0.588        0.625        0.606
      PRON          14     1.000        0.929        0.963
       PRT          45     0.957        0.978        0.967
   PRT+PRT           2     0.400        1.000        0.571
        QP          26     0.647        0.423        0.512
         S         587     0.626        0.782        0.695
      SBAR          25     0.091        0.040        0.056
      VERB         283     0.683        0.799        0.736
        VP         399     0.559        0.594        0.576
   VP+VERB          15     0.250        0.267        0.258

     total        4664     0.714        0.714        0.714

Took 2466347300610 ns (41 minutes) to run the improved algorithm on question
6.
