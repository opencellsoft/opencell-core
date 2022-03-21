What is Liquibase?
-------------------
Liquibase is an open source database change management tool built on Java. Rather than writing SQL directly against the database to create, update or drop database objects, 
developers define their desired database changes in XML files. The XML file, called a changelog, contains a list of changesets that define a desired database change in an database agnostic abstraction. 
The changelog is intended to contain an evolving list of database changes the team would like to apply to a target database. This list is additive over time.


How does it work in Opencell?
------------------------------
Liquibase can be executed through either the command line or as part of a build using Ant, Maven or the like (build integration). 
Liquibase will apply the changesets directly to the database and can handle rollbacks and tagging of database state.

Directory Structure:
1- /current - the directory that contains :
structure.xml : the evolution changelog file which evolves by a release number.
data.xml : the dataset file related to changelog file structure.xml.

2- /rebuild : the directory that contains :
structure.xml : the initial changelog file that creates the database.
data.xml : base dataset file related to changelog file structure.xml. Should contain only a minimum data needed to start opencell application.
data-scripts.xml : scripts dataset related to changelog file structure.xml.
data-reports.xml : reports dataset related to changelog file structure.xml.
data-demo.xml : demo dataset

3- /dunning : the directory that contains :
data.xml : demo dunning related dataset

4- db.current.xml : master changelog file that we pass all current Liquibase calls.
5- db.rebuild.xml : master changelog file that we pass all initial Liquibase calls.
6- db.base.xml : master changelog file that we pass initial Liquibase calls without scripts, reports and demo data.
7- db.dunning.xml : master changelog file to populate database with demo dunning data


Procedure for the developer
------------------------------
running rebuild: 
mvn sql:execute@reset-pg -Pdevelopment,rebuild liquibase:dropAll liquibase:update
for oracle replace sql:execute@reset-pg with sql:execute@reset-oracle

running update: 
mvn -Pdevelopment liquibase:clearCheckSums liquibase:update

updating data model:
1- edit rebuild file by adding corresponding changes to model definitions
2- add data model modification changeset to current file 
3- add empty changeset to the end of rebuild file with the same id and author that was added as to current file


Best Practices
------------------------------
- One Change per ChangeSet
- Take attention to duplication of changeSet Ids and authors, the pattern of id is : #IdOfYourTicket_yyyymmdd
- Make your changesets database agnostic by using variables defined in db.current.xml or db.rebuild.xml files


How to generate a sql delta between two different local versions?
------------------------------------------------------------------
we'll take an example of two versions 11.0.0 and 11.1.0

1- Make a pull rebase of the 11.0.0 branch

2- Run dropAll and update liquibase command to reinstall the local database to version 11.0.0 by executing this maven command : 
	mvn sql:execute@reset-pg -Pdevelopment,rebuild liquibase:dropAll liquibase:update

3- Make a checkout and pull rebase of the 11.1.0 branch

4- Run updateSQL liquibase command to install the local database to version 11.1.0 and generate a sql delta between 11.0.0 and 11.1.0 by executing this maven command : 
	mvn -Pdevelopment liquibase:clearCheckSums liquibase:updateSQL


------------------------------------------------------------------
For more informations check this video training:
https://opencellsoft-my.sharepoint.com/:v:/r/personal/abdelmounaim_akadid_opencellsoft_com/Documents/Enregistrements/Formation%20liquibase-20211028_100548-Meeting%20Recording.mp4?csf=1&web=1&e=WVUJcC
------------------------------------------------------------------



