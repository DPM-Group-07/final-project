# final-project
This is the main repository for the final project. Documentation can be found [here](https://dpm-group-07.github.io/final-project/index.html).

*Note that the documentation is compiled from the master branch.*

## Branches

* master: This is the main branch. Commits cannot be pushed to it. Code can only be pushed to this branch through a pull request. Any code that is pushed to this branch must already be tested and working.

* dev: Development branch. This code is not tested. Any changes to the code are pushed to this branch before being pulled in to master.

## How to clone and run this project

1. Go to your Eclipse workspace, open a command prompt, and run the following command: `git clone https://github.com/DPM-Group-07/final-project.git`

2. (Optional) If you are looking for the "dev" branch, change directories and switch branches: `cd final-project` and  `git checkout dev`

3. Open Eclipse. If final-project appears in the Package Explorer on the left (it will), delete it. Don't delete it from the disk though. (Don't check the check box.) Right click on "final-project" and select Delete.

4. File -> New -> Java Project. Enter "final-project" as the project name and click Finish. Eclipse will create a new project and import the source.

5. Right-click on the newly created project and convert it to a LeJOS EV3 project.

6. (You might not need to do this) Because we are using an external library for JSON objects (json-simple-1.1.1.jar), you need to add it to the build path. Expand the "lib" folder under the project in the package explorer. Right-click on "json-simple-1.1.1.jar" and select Build Path -> Add to Build Path.

7. Right click on the project name in the Package Explorer and select Properties. Go to Java Compiler, check Enable project specific settings, and set the compiler compliance level to 1.7.

## How to compile javadocs

You need to add the leJOS EV3 "ev3classes.jar" file to your project classpath, else javadoc will throw warnings at you. To do so, open the ".classpath" file with any file editor (outisde of Eclipse). Add the following line to the file.

`<classpathentry kind="lib" path="C:\lejos EV3\lib\ev3\ev3classes.jar"/>`

The "path" value will of course depend on where you chose to install leJOS. I chose C:\lejos EV3.

* Re-open the project in Eclipse, and go to Project -> Generate Javadoc...

* Make sure final-project is CHECKED (checkmark - not a square) in the project list.

* Make sure the javadoc destination folder is docs, not doc.  (It is doc by default)

* Click Finish, and everything should compile.

*(Or just ask Max to do it)*
