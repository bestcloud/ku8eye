var $proObj={"projectName":"projectname","version":"1","author":"author","k8sVersion":"1","notes":"note","services":[{"name":"servicename","describe":"describe","tag":"tag","replica":"12","version":"0.1","containerPort":1100,"servicePort":1300,"nodePort":1200,"image":[{"name":"name","version":"0.1","registry":"d:/registry","imageName":"imageName","command":"bin","quotas_limits":"100","quotas_cpu":"quotas_cpu","quotas_memory":"1230mb"}],"envVariable":[{"name":"var1name","value":"val1val"}]}]}

function createServiceJson() {
	var $ServiceJson = {};

	var $servicePanel = $("div[name='servicePanel']");

	for (var i = 0; i < $servicePanel.length; i++) {
		var $service = $servicePanel.eq(i);
		var s_name = $service.find("input[id='s_name']").val();
		var describe = $service.find("input[id='describe']").val();
		var replica = $service.find("input[id='replica']").val();
		var s_version = $service.find("input[id='s_version']").val();
		$ServiceJson.name = s_name;
		$ServiceJson.describe = describe;
		$ServiceJson.replica = replica;
		$ServiceJson.version = s_version;
		var $images = $service.find("div[name='image']");
		var ims = [];
		$ServiceJson.images = ims;
		for (var j = 0; j < $images.length; j++) {
			var $image = $images.eq(j);
			var img_image = $image.find("select[id='img_image']").val();
			var img_limits = $image.find("input[id='img_limits']").val();
			var img_cpu = $image.find("input[id='img_cpu']").val();
			var img_memory = $image.find("input[id='img_memory']").val();
			var $image = {};
			$image.id = img_image;
			$image.quotas_limits = img_limits;
			$image.quotas_cpu = img_cpu;
			$image.quotas_memory = img_memory;
			ims.push($image);
		}
		var containerPort = $service.find("input[id='containerPort']").val();
		var servicePort = $service.find("input[id='servicePort']").val();
		var nodePort = $service.find("input[id='nodePort']").val();
		$ServiceJson.containerPort = containerPort;
		$ServiceJson.servicePort = servicePort;
		$ServiceJson.nodePort = nodePort;

		var $envs = $($ServiceJson).find("div[name='envVariable']");
		var ens = [];
		$ServiceJson.envVariables=ens;
		for (var k = 0; k < $envs.length; k++) {
			var $env=$envs.eq(k);
			var envName=$image.find("input[id='envName']").val();
			var envValue=$image.find("input[id='envValue']").val();
			var $en={};
			$en.name=envName;
			$en.value=envValue;
			ens.push($en);
		}
	}

	return $ServiceJson;
}