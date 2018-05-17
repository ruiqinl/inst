pipeline {
  agent none
  
  stages {
    stage('Build') {
      parallel {
        stage('Build') {
            agent {
    docker {
      image 'gradle:alpine'
    }

  }
          
          steps {
            sh 'gradle -v'
          }
        }
        stage('Build2') {
            agent {
    docker {
      image 'gradle:alpine'
    }

  }
          
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
