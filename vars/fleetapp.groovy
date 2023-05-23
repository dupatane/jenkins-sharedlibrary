def call(String registryCred = 'a', String registryin = 'dupatane/docker', String docTag = 'fleet', String grepo = 'https://github.com/dupatane/kubernetes-fleetapp-project.git', String gbranch = 'main', String gitcred = 'a') {

pipeline {
environment { 
		registryCredential = "${registryCred}"
		registry = "$registryin" 	
		dockerTag = "${docTag}$BUILD_NUMBER"
		gitRepo = "${grepo}"
		gitBranch = "${gbranch}"
		gitCredId = "${gitcred}"
	}
		
    agent any

    stages {
        stage("POLL SCM"){
            	steps {
                	checkout([$class: 'GitSCM', branches: [[name: "$gitBranch"]], extensions: [], userRemoteConfigs: [[credentialsId: "$gitCredId", url: "$gitRepo"]]])             
            	}
        } 
        stage('BUILD IMAGE') {
            	steps {
			sh 'cd "/var/lib/jenkins/workspace/fleetapp/sourcecode/k8s-fleetman-api-gateway" && docker build -t $registry:$dockerTag .'           
            	}
        }
        stage('PUSH HUB') { 
            	steps {
			sh 'docker login'
			sh 'docker push $registry:$dockerTag'                   	
                }    
        } 
    }
}  
}
