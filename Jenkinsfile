pipeline {
	agent any

	tools {
		jdk "openjdk-17"
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Build') {
                    steps {
                        sh './gradlew clean build'
                    }
                }

		stage('Test') {
			steps {
				timeout(time: 15, unit: 'MINUTES') {
					script {
						def testExitCode = sh(returnStatus: true, script: './gradlew test')
						if (testExitCode != 0) {
							def currentBranch = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
							if (currentBranch == 'main') {
								currentBuild.result = 'FAILURE'
							} else {
								currentBuild.result = 'UNSTABLE'
							}
						} else {
							currentBuild.result = 'SUCCESS'
						}
					}
				}
			}
		}
	}

	post {
		always {
            archiveArtifacts artifacts: '**/build/reports/*', allowEmptyArchive: true
        }
	}
}