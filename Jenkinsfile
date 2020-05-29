pipeline {
    agent any
    tools {
      // Install the Maven version configured as "M3" and add it to the path.
      maven "M3"
   	}
    stages {
     

   
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                // file:///home/miguelamartin/eclipse-proyectos/tic-tac-toe-enunciado/
                //git 'https://github.com/miguelammm/ingswalert.git'
            	git 'git@github.com:miguelammm/ingswalert.git'

            	// Run Maven on a Unix agent.
            	sh "mvn -Dmaven.test.failure.ignore=true clean package"
            }
           post {
            // If Maven was able to run the tests, even if some of the test
            // failed, record the test results and archive the jar file.
            success {
               junit '**/target/surefire-reports/TEST-*.xml'
               archiveArtifacts 'target/*.jar'
            }
         }
        }

    }
}
