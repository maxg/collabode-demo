Collabode Demo
==============

http://uid.csail.mit.edu/collabode

Collabode is a web-based collaborative software development environment
powered by Eclipse and EtherPad.

This repository contains an automated demo.

**Collabode source: https://github.com/uid/collabode**


Generating Eclipse configuration files
--------------------------------------

    mvn eclipse:clean eclipse:eclipse replacer:replace


Running the demo
----------------

    mvn clean compile exec:java -Dexec.mainClass=Demo
