(ns ^:integration namespace
  (:use [conjecture.core]
        [selectors :only [record-ran]]))

(deftest integration-test
  (record-ran :integration-ns)
  (is true))

(deftest ^:int2 int2-integration-test
  (record-ran :integration-ns)
  (is true))
