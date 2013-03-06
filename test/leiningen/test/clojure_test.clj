(ns leiningen.test.clojure-test
  (:use [name.stadig.clojure.test]
        [leiningen.clojure-test]
        [leiningen.test.helper :only [tmp-dir sample-no-aot-project]])
  (:require [clojure.java.io :as io]))

(use-fixtures :each
              (fn [f]
                (f)
                (.delete (java.io.File. tmp-dir "lein-test-ran"))))

(defn ran? []
  (let [ran-file (io/file tmp-dir "lein-test-ran")]
    (and (.exists ran-file)
         (set (for [ran (.split (slurp ran-file) "\n")]
                (read-string ran))))))

(deftest test-project-selectors
  (is (= [:default :integration :int2 :no-custom]
         (keys (:test-selectors sample-no-aot-project))))
  (is (every? ifn? (map eval (vals (:test-selectors sample-no-aot-project))))))

(deftest test-default-selector
    (clojure-test sample-no-aot-project ":default")
    (is (= (ran?) #{:regular :int2 :not-custom})))

(deftest test-basic-selector
  (clojure-test sample-no-aot-project ":integration")
  (is (= (ran?) #{:integration :integration-ns})))

(deftest test-complex-selector
  (clojure-test sample-no-aot-project ":no-custom")
  (is (= (ran?) #{:integration :integration-ns :regular :int2})))

(deftest test-two-selectors
  (clojure-test sample-no-aot-project ":integration" ":int2")
  (is (= (ran?) #{:integration :integration-ns :int2})))

(deftest test-override-namespace-selector
  (clojure-test sample-no-aot-project ":int2")
  (is (= (ran?) #{:integration-ns :int2})))

(deftest test-only-selector
  (clojure-test sample-no-aot-project ":only" "selectors/regular")
  (is (= (ran?) #{:regular})))

(def called? (atom false))

(defmethod name.stadig.clojure.test/report :begin-test-ns [_]
  (reset! called? true))

(deftest test-report-call-through
  (is (true? @called?))
  (reset! called? false))

