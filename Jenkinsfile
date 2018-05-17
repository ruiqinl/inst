pipeline {
  agent {
    docker {
      image 'gradle:alpine'
    }

  }
  stages {
    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            sh 'gradle -v'
          }
        }
        stage('Build2') {
          steps {
            echo 'branch2'
          }
        }
      }
    }
    stage('Test') {
      steps {
        echo 'test'
      }
    }
  }
}