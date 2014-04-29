NemakiWare Version 1.0GA3
======================

What is NemakiWare?
------

NemakiWare is an open source Enterprise Content Management (ECM) system.
It is:
* **Compliant with the CMIS standard**
    * Integration or replacement with other existing/future CMIS-compliant software can be done easily thanks to the standard.


* **All-in-One package (Server, Client, Full-text search engine, archive feature etc.)**
    * CMIS defines just the core of ECM. You need some peripheral components/feature, and here it is.
    * Not only the server but the client and the search engine are connected via the CMIS interface.


* **NoSQL CouchDB backend**
    * Simple document-based NoSQL database, suitable for DMS.
    * Easy Database management and replication.


"Nemaki" derives from Japanese word "寝巻き" which means night clothes.
You can relax and enjoy your happy enterprise time as you are lying on the couch in your room!


Prerequisite for installation
------
* Java 1.6(JDK), Maven 3.x, Ruby 1.9.x, Rails 3.2.x, sqlite 3.7.x, python 2.7.x
  * The server and Solr are written in Java, and the client in RoR.
  * Python is needed only for importing data during setup.
* Platform: OSX 10.8.3+ or CentOS 6+, Ubuntu 12.10+ (Although Windows is not tested, NemakiWare is basically platform-agnostic).
* Package management system, yum (CentOS), Homebrew/Mac Port (Mac), apt-get (Ubuntu) as you like

### On Ubuntu 
```bash
sudo apt-get install curl vim git openjdk-6-jdk maven ruby1.9.1 ruby1.9.1-dev sqlite3 libsqlite3-dev -y
```

Installation
------
### Clone the repository

```sh
$ git clone https://github.com/NemakiWare/NemakiWare.git
```
You get the folder `<NemakiWare_Home>` in the location you cloned, which includes three subfolders nemakiware, nemakisolr, nemakishare.

### Install CouchDB 
Version 1.0.4 or higher is desirable.

#### Mac
(See [Wiki](http://wiki.apache.org/couchdb/Installing_on_OSX))
```sh
$ sudo port install couchdb
```

#### CentOS
(See [Wiki](http://wiki.apache.org/couchdb/Installing_on_RHEL5))
Firstly [enable EPEL repository](http://wiki.apache.org/couchdb/Installing_on_RHEL5), then
```sh
$ sudo yum install couchdb
```
Start CouchDB
```sh
$sudo service couchdb start
```
(Option)Set it to start automatically on reboots
```sh
$sudo chkconfig --level 345 couchdb on
```

#### Ubuntu
(See  [Wiki](http://wiki.apache.org/couchdb/Installing_on_Ubuntu))
```sh
$ sudo apt-get install couchdb
```

### [Install Applications For Production Environment(tomcat)]
```sh
$ <NemakiWare_Home>/installer/nemakiware_installer.bin
$ <NemakiWare_Home>/installer/nemakisolr_installer.bin
$ <NemakiWare_Home>/installer/nemakishare_installer.bin
```

### Start the applications
```sh
$ sh <NemakiWare/Installed/Path>/tomcat/bin/startup.sh
$ cd <NemakiWare/Installed/Path>/nemakishare
$ rails s
```

To stop the applications,
```sh
$ <NemakiWare/Installed/Path>/tomcat/bin/shutdown.sh
```

### [Install Applications For Development Environment(jetty)]

### Execute setup script 
If CouchDB has not started, start it before setup.

```sh
$ cd <NemakiWare_Home>/setup
$ sudo sh setup.sh
```

  NOTE: The script have installed [ActiveCMIS](https://github.com/xaop/activecmis) gem.
NemakiWare needs to overwrite some part of ActiveCMIS library but it's not yet pull requested.

### Start the applications
```sh
$ cd <NemakiWare_Home>/setup
$ sh start.sh
```
It will take a little time to download the dependent packages.
Server (nemakiware), Solr (nemakisolr), Client (nemakishare) have been now started.

To stop the applications,
```sh
$ sh stop.sh
```

### Login 
Now, open the login window at http://127.0.0.1:3000/nodes/(default)

* ID:admin
* Password admin

That's all!


Usage
----------
* Show/Edit/Manage permission/Delete/Search
  * There are action buttons next to a content in the explore list. There actions may be different for each content and each user because an action requires its permission.
  * Custom properties(or Aspects) are available in addition to CMIS basic properties.  
   Administrator can define custom properties and user can attach them to an object as he/she likes.  
   But there is not now any other way to define the custom modesl other than registering them directly onto the DB. 
  * Edit page shows updatable properties and new version upload for a document.  
   CheckOut/CheckIn is not available now (although, in fact, it is implemented in the server as beta version).
  * NemakiWare implements permissions for each content as ACL, which is supported by CMIS. CMIS default permission is `cmis:read`, `cmis:write` and `cmis:all`.  
   In addtion, NemakiWare allows custom permissions described in `<NemakiWare_Home>/nemakiware/src/main/resources/custom_permission.yml`. Specify the "base" permission and modify it by adding custom allowable actions in "permissionMapping". As custom permission feature is still beta version, it might not work as expected.
  * Search form is situated on the top of the window. Only simple search is supported now. A query goes to search name and full text for both document and folder.

* Navigation
  * You can drill down the folder hierarchy by clicking folder in the explore list, or by the navigation bar. Breadcrumbs and up/next/previous are available.
  * Sorting items is not available for the time being but will be implemented.
  * Search results also show which site each result belongs to.

* Site
  * "Site" is a space where users can share documents and collaborate on them. In NemakiWare, it is realized as a simple folder under the special "Site Root" folder and there is the latest changes column in the site. "Site" is out of scope of CMIS specifications and implemented on the client, not the server layer.
  * Add/Remove members to a site can be done by managing permissions. Who can access the site folder is considered as a member.

* Administration  
  In the navigation bar on top of the window, there is a dropdown button displaying the user name.
  If you are admin, administration menu are also displayed.
    * User/Group management
      * User/Group CRUD, including add/remove members to a group
    * Solr management
      * Initialize search engine index & reindex
    * Archive
      * Can confirm which documents/folders are deleted/restore each object.

Configuration
----------
* CouchDB
  CouchDB may be working on "http://localhost:5984/".

  * If you have changed the address or port,
   In `<NemakiWare_Home>/nemakiware/src/main/webapp/WEB-INF/classes/nemakiware.properties`  
   modify `db.protocol`, `db.host`, `db.port` (and also `db.maxConnections`).  
   In `<NemakiWare_Home>/nemakisolr/src/main/webapp/WEB-INF/classes/nemakisolr.properties`  
   modify `couchdb.server.url`, `couchdb.db.repository`, `couchdb.maxconnection`.

  * Secure connection to CouchDB(Basic Auth, Reverse Proxy etc.) is not supported by NemakiWare for the present.
  * If you want to manage CouchDB directly, Futon UI "http://localhost:5984/_utils/" is useful.
  CouchDB database `bedroom` contains all of the data except for deleted(archived) data in `archive`.
  These database names are configured by `nemakiware.repository.main` and `nemakiware.repository.archive` in `<NemakiWare_Home>/nemakiware/src/main/webapp/WEB-INF/classes/nemakiware.properties`.  
   `nemakiware.repository.main` is also used as the CMIS serever repository id.

* Solr
  Solr may be working on "http://localhost:8983/solr".
  *  If you have change the address or port,
    In `<NemakiWare_Home>/nemakiware/src/main/webapp/WEB-INF/classes/nemakiware.properties`
  modify `solr.url`.
    And in `<NemakiWare_Home>/nemakishare/config/nemakiware_config.yml`
  modify `search_engine: server_url` key, though it is only used for Client's administration use.

  * Secure connection to CouchDB(Basic Auth, Reverse Proxy etc.) is not supported by NemakiWare for the present.

  * NemakiSolr crawls CouchDB changes by cron. The interval is set by `tracking.cron.expression` in `<NemakiWare_Home>/nemakisolr/src/main/webapp/WEB-INF/classes/nemakisolr.properties`.

  * NemakiSolr support full-text index for the specified mime-type files. These types are described in `tracking.mimetype`.

* CMIS Server
  Nemaki CMIS server may be working on "http://localhost:8080/Nemaki/atom/bedroom/".
  *  If you have change the address or port,
  In `<NemakiWare_Home>/nemakishare/config/nemakiware_config.yml`
  modify URLs in `repository` key.

  * `site: root_id` specifies "Site Root" folder object id. Its subfolders are treated as a site in NemakiWare client.


Development
----------
Before importing projects of the server(nemakiware) and the search engine(nemakisolr) to Eclipse,
```sh
$ mvn eclipse:eclipse
```

To start each app, for server(nemakiware) and solr(nemakisolr),
```sh
$ mvn jetty:run
```

for client(nemakishare),
```sh
$ rails s
```

There is no restriction for the order of startup as long as CouchDB is started before the server and the solr.

License
----------
Copyright (c) 2013 aegif.

NemakiWare is Open Source software and licensed under the `GNU General Public License version 3 or later`. You are welcome to change and redistribute it under certain conditions. For more information see the `legal/LICENSE` file.

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/NemakiWare/NemakiWare/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
