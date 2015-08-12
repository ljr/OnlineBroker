# Installation notes for Eclipse #

* Install Eclipse J2EE (Or any version with Eclipse Marketplace): http://www.eclipse.org/downloads/

* Install Maven

* Install Maven from Eclipse Marketplace: Eclipse > Help > Eclipse Marketplace > Search: Maven > Install: Maven Integration for Eclipse

* Install EGit from Eclipse Marketplace: Eclipse > Help > Eclipse Marketplace > Search: EGit > Install: EGit - Git Team Provider

* Add git connector to Maven: Eclipse > Window > Preferences > Maven > Discovery > Open Catalog > Check: m2e-egit > Finish

* Install CloudSim as a Maven Project

* Go to `workspace/cloudsim-package` directory and execute: `mvn install`

* Install OnlineBroker as a Maven Project

Reference: [CloudSimEx](https://github.com/Cloudslab/CloudSimEx/wiki/Installation)