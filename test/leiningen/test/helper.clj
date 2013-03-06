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
(ns leiningen.test.helper
  (:require [leiningen.core.project :as project]
            [leiningen.core.user :as user]
            [clojure.java.io :as io]))

;; TODO: fix
(def local-repo (io/file (System/getProperty "user.home") ".m2" "repository"))

(def tmp-dir (System/getProperty "java.io.tmpdir"))

(defn m2-dir [n v]
  (io/file local-repo
           (if (string? n) n (or (namespace n) (name n))) (name n) v))

(defn- read-test-project [name]
  (with-redefs [user/profiles (constantly {})]
    (project/init-project
     (project/read (format "test_projects/%s/project.clj" name)))))

(def sample-no-aot-project (read-test-project "sample_no_aot"))

;; grumble, grumble; why didn't this make it into clojure.java.io?
(defn delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its contents.
Raise an exception if any deletion fails unless silently is true."
  [f & [silently]]
  (System/gc) ; This sometimes helps release files for deletion on windows.
  (let [f (io/file f)]
    (if (.isDirectory f)
      (doseq [child (.listFiles f)]
        (delete-file-recursively child silently)))
    (io/delete-file f silently)))
