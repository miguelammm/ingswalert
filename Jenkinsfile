pipeline {
    agent any

    stages {
        agent any
      environment {
        AWS_ACCESS_KEY_ID     = credentials('miguelid')
        AWS_SECRET_ACCESS_KEY = credentials('123456')
    }      
        tools {
      		// Install the Maven version configured as "M3" and add it to the path.
      		maven "M3"
   	}
   
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
            	git 'https://github.com/miguelammm/tictactoe.git'

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
