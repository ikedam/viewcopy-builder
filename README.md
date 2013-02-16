Viewcopy Builder plugin
=======================

Japanese version of this document is README_ja.md

Jenkins plugin to copy a view in a build step.

What's this?
------------

Viewcopy Builder is a [Jenkins](http://jenkins-ci.org/) plugin.
This plugin provides Copy View build step:

* It makes a new view from an existing view.
	* This can be configured as a build step, so you can copy multiple views in one build execution with multiple build steps.
* You specify following parameters.
	* From View Name
		* Variable expressions can be used.
	* To View Name
		* Variable expressions can be used.
	* Overwite
		* Specifies whether to overwrite if the destination view already exists.
* Additional operations will be performed when copying.
	* Replace String: Replace strings in a view configuration.
		* Source and destination strings can contain variable expressions.
	* Set Regular Expression: Set the regular expression of ListView.
	* Set Description: Set the description of the view.
* Additional operation can be extended by using [the Jenkins extention point featere] (https://wiki.jenkins-ci.org/display/JENKINS/Extension+points).

Limitations
-----------

* The job contains Copy View build steps must run on the master node.

How does this work?
-------------------

This plugin works as following:

1. Generate configuration xml of the copying view using XSTREAM.
2. Applies the operations to the configuration xml.
3. Create a new view with the processed configuration xml.

Extension point
---------------

New additional operations can be added with extending `ViewcopyOperation`, overriding the following method:

```java
public abstract Document ViewcopyOperation::perform(Document doc, EnvVars env, PrintStream logger)
```
