# final-project
This is the main repository for the final project.

## Branches

* master: This is the main branch. Commits cannot be pushed to it. Code can only be pushed to this branch through a pull request. Any code that is pushed to this branch must already be tested and working.

* dev: Development branch. This code is not tested. Any changes to the code are pushed to this branch before being pulled in to master.

## How to clone and run this project

1. Go to your Eclipse workspace, open a command prompt, and run the following command: `git clone https://github.com/DPM-Group-07/final-project.git`

2. (Optional) If you are looking for the "dev" branch, change directories and switch branches: `cd final-project` and  `git checkout dev`

3. Open Eclipse. If final-project appears in the Package Explorer on the left (it will), delete it. Don't delete it from the disk though. (Don't check the check box.) Right click on "final-project" and select Delete.

4. File -> New -> Java Project. Enter "final-project" as the project name and click Finish. Eclipse will create a new project and import the source.

5. Right-click on the newly created project and convert it to a LeJOS EV3 project.

6. Because we are using an external library for JSON objects (json-simple-1.1.1.jar), you need to add it to the build path. Expand the "lib" folder under the project in the package explorer. Right-click on "json-simple-1.1.1.jar" and select Build Path -> Add to Build Path.

7. Done
