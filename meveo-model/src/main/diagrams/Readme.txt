Current folder contains JPA data model diagrams. Diagrams were developed using "Dali Java persistence tools - JPA diagram editor" from http://download.eclipse.org/releases/kepler. Eclipse marketplace also offers a version to installed, but it is an older version.

More info on editor is available at https://wiki.eclipse.org/JPA_Diagram_Editor_Project_Tutorial.

Steps to make diagram editor to work:
1. Enable JPA facet on meveo-model project. In JPA facet setup configure to not use library configuration.
2. Install JPA diagram editor. Set "src\main\diagrams" as folder for diagrams in JPA diagram editor preferences.
3. Editor supports only one diagram per project matching project's name. Rename desired model to meveo-model.xml and right-click on "project>JPA tools>Open diagram" to view and edit diagram. Rename it back to a previous file once done. 

Note: Once JPA faclet is enabled addition xxx_ classes will be created and other files changed. Ignore and revert these changes one done.