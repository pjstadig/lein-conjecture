;;;; Copyright © Phil Hagelberg and Paul Stadig. All rights reserved.
;;;;
;;;; The use and distribution terms for this software are covered by the Eclipse
;;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;;;; can be found in the file epl-v10.html at the root of this distribution.
;;;;
;;;; By using this software in any fashion, you are agreeing to be bound by the
;;;; terms of this license.
;;;;
;;;; You must not remove this notice, or any other, from this software.
(ns leiningen.test.conjecture
  (:use [conjecture.core]
        [leiningen.conjecture]
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
    (conjecture sample-no-aot-project ":default")
    (is (= (ran?) #{:regular :int2 :not-custom})))

(deftest test-basic-selector
  (conjecture sample-no-aot-project ":integration")
  (is (= (ran?) #{:integration :integration-ns})))

(deftest test-complex-selector
  (conjecture sample-no-aot-project ":no-custom")
  (is (= (ran?) #{:integration :integration-ns :regular :int2})))

(deftest test-two-selectors
  (conjecture sample-no-aot-project ":integration" ":int2")
  (is (= (ran?) #{:integration :integration-ns :int2})))

(deftest test-override-namespace-selector
  (conjecture sample-no-aot-project ":int2")
  (is (= (ran?) #{:integration-ns :int2})))

(deftest test-only-selector
  (conjecture sample-no-aot-project ":only" "selectors/regular")
  (is (= (ran?) #{:regular})))

(def called? (atom false))

(defmethod conjecture.core/report :begin-test-ns [_]
  (reset! called? true))

(deftest test-report-call-through
  (is (true? @called?))
  (reset! called? false))

