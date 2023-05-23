def call(String registryCred = 'a', String registryin = 'a', String docTag = 'a', String grepo = 'a', String gbranch = 'a', String gitcred = 'a') {

    pipeline {
        agent any

        stages {
            stage("POLL SCM"){
                steps {
                    checkout([$class: 'GitSCM', branches: [[name: gbranch]], extensions: [], userRemoteConfigs: [[credentialsId: gitcred, url: grepo]]])             
                }
            } 
            stage('BUILD IMAGE') {
                steps {
                    sh 'cd "/var/lib/jenkins/workspace/fleetapp/sourcecode/k8s-fleetman-api-gateway" && docker build -t $registryin:$docTag .'           
                }
            }
            stage('PUSH HUB') { 
                steps {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]){
                        sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                        sh 'docker push $registryin:$docTag'                   	
                    }    
                }
            } 
        }
    }  
}
