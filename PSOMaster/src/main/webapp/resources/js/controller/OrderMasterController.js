module.controller("OrderMasterController", function($scope, $routeParams,$http,OrderService,MessageService,AppDataService,$rootScope,CommonUtils) {
	
	$scope.title = "Order Master";
	
	$scope.orderID = $routeParams.orderID;
	
	$scope.portalOrderDetails = {};
	$scope.ensembleOrderDetails = {};
	$scope.apiOrderDetails= [];
	
	/*
	 * Method that calls Order Service to get Portal and Ensemble Order Details 
	 */
	$scope.searchOrderDetails = function(){
		
		var orderID = $scope.orderID;
		
		if(orderID != undefined && orderID.length>0){
			$rootScope.spinner.on();
			// Get Portal Order Details
			getPortalOrderDetails(orderID);
			
			// Get Portal Order API Details
			getPortalOrderAPIDetails(orderID);
			
			// Get Ensemble Order Details
			getENSOrderDetails(orderID);
		}
		else{
			MessageService.showInfo("Please Enter Order ID",5000);
			return;
		}
		
		/*MessageService.showSuccess('This is Success Message',5000);
		MessageService.showError('This is Error Message',6000);
		MessageService.showInfo('This is Info Message',7000);
		MessageService.showWarning('This is Warning Message',8000);*/
	}
	
	/*
	 * Method that calls Order Service to get Port status for the selected line
	 */
	$scope.getPortStatus = function(){
		$scope.portalOrderPortInDetails = [];
		$scope.ensOrderPortInDetails = [];
		
		var anyPortInLineAvailable = checkForPortInLines();
		
		if(anyPortInLineAvailable)
		{
			$("#orderPortDetails-modal").modal();
			
			OrderService.getPortInStatus($scope.orderID).then(
					function(response) {
						if(response.errorCode==0){
							$scope.portalOrderPortInDetails = response.portalPortReqDetailsList;
							if(response.ensPortReqDetailsList!=null)
								$scope.ensOrderPortInDetails = response.ensPortReqDetailsList;
						}
						else if(response.errorCode==1)
							MessageService.showInfo('No Portal Port In details found.',5000);
						else if(response.errorCode==-1)
							MessageService.showError(response.errorMsg,5000);
						
		       		},
			        function(errResponse){
		       			MessageService.showError(errResponse,5000);
			        }
			);
		}
		else
		{
			MessageService.showInfo('No Port-In line available for this Order',5000);
		}
	}
	
	function checkForPortInLines(){
		
		if($scope.portalOrderDetails.portalLineItemList==null)
			return false;
		
		var keepChecking = true;
		var anyPortInLinePresent = false;
		angular.forEach($scope.portalOrderDetails.portalLineItemList,function(obj,index){
			if(keepChecking)
			{
				if(obj.lineType === 'PORTIN')
				{
					keepChecking = false;
					anyPortInLinePresent = true;
				}
			}	
        })
		
		return anyPortInLinePresent;
	}
	
	
	/*
	 * Method that calls Order Service to get Portal Order Details 
	 */
	function getPortalOrderDetails(orderID){
		$scope.portalOrderDetails = [];
		OrderService.getPortalOrderDetails(orderID).then(
				function(response) {
					$rootScope.spinner.off();
					if(response.errorCode==0){
						$scope.portalOrderDetails = response;
					}	
					else if(response.errorCode==1)
						MessageService.showInfo('No Portal Order details found.',5000);
					else if(response.errorCode==-1)
						MessageService.showError(response.errorMsg,5000);
					
	       		},
		        function(errResponse){
	       			$rootScope.spinner.off();
	       			MessageService.showError(errResponse,5000);
		        }
		);
		
	}
	
	/*
	 * Method that calls Order Service to get Portal Order API Details 
	 */
	function getPortalOrderAPIDetails(orderID){
		$scope.apiOrderDetails = [];
		OrderService.getPortalAPIDetails(orderID).then(
				function(response){
					if(response.errorCode==0){
						$scope.apiOrderDetails=response.orderAPIList;
						
					}	
					else if(response.errorCode==1)
						MessageService.showInfo('No API Details found.',5000);
					else if(response.errorCode==-1)
						MessageService.showError(response.errorMsg,5000);
					
					
				},
				function(errResponse){
				}
		);
		
	}
	
	/*
	 * Method that calls Order Service to get Ensemble Order Details 
	 */
	function getENSOrderDetails(orderID){
		$scope.ensembleOrderDetails = [];
		OrderService.getEnsembleOrderDetails(orderID).then(
				function(response) {
					if(response.errorCode==0)
						$scope.ensembleOrderDetails = response;
					else if(response.errorCode==1)
						MessageService.showInfo('No Ensemble details found',5000);
					else if(response.errorCode==-1)
						MessageService.showError(response.errorMsg,5000);
	       		},
		        function(errResponse){
	       			MessageService.showError(errResponse,5000);
		        }
		);
		
	}
	
	
	if($scope.orderID!=undefined){
		$scope.searchOrderDetails();
	}
	
	
	$scope.orderAPIReqBody = null;
	$scope.orderAPIReqPanelTitle = null;
	$scope.getAPIRequestResponse = function(seq_number,callType){
		$rootScope.spinner.on();
		OrderService.getOrderAPIRequestResponse(seq_number,callType).then(
				function(response) {
					$rootScope.spinner.off();
					//$scope.orderAPIReqBody  = JSON.stringify(response, undefined, 4);
					var final_XML = '';
					var isValidXml = CommonUtils.isValidXML(response);
					if(isValidXml){
						var xml_formatted = CommonUtils.formatXml(response);
						var final_XML = xml_formatted.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/ /g, '&nbsp;').replace(/\n/g,'<br />');
					}
					else
						final_XML = JSON.stringify(response, undefined, 4);

					$scope.orderAPIReqPanelTitle = 'API ' + callType;
					document.getElementById("apiReqResBody").innerHTML = final_XML;
					//$scope.orderAPIReqBody = response;
					$("#orderApiReqBody-modal").modal();
	       		},
		        function(errResponse){
	       			
		        }
		);
	}
	
	$scope.orderHelpData = null;
	$scope.getHelp = function(){
		AppDataService.getOrderHelpData()
			.then(function(response){
						$scope.orderHelpData = response.data;
						$("#orderHelp-modal").modal();
					}, 
					function(errResponse){
						return $q.reject(errResponse);
					}
			);
	}
	
	$scope.getInvStatusDetails = function(){
		getInventoryStatusDetails($scope.skuIDInvSearch);
	}
	
	$scope.getInventory = function(){
		$('#inventoryDetail-modal').modal();
	}
	
	
	/*
	 * Method that calls Order Service to get Ensemble Order Details 
	 */
	function getInventoryStatusDetails(skuID){
		$scope.inventoryStatusDetails = [];
		OrderService.getInventoryStatusDetails(skuID).then(
				function(response) {
					if(response.errorCode==0){
						$scope.inventoryStatusDetails = response;
					}
					else if(response.errorCode==1)
						MessageService.showInfo('No Inventory details found',5000);
					else if(response.errorCode==-1)
						MessageService.showError(response.errorMsg,5000);
	       		},
		        function(errResponse){
	       			MessageService.showError(errResponse,5000);
		        }
		);
		
	}
	
	$scope.getNameDetails = function(){
		getNameDetails($scope.esnNameSearch);
	}
	
	$scope.getName = function(){
		$("#nameDetail-modal").modal();
	}
	
	
	/*
	 * Method that calls Order Service to get Ensemble Order Details 
	 */
	function getNameDetails(esn){
		$scope.nameDetails = [];
		OrderService.getNameDetails(esn).then(
				function(response) {
					if(response.errorCode==0){
						$scope.nameDetails = response;
					}
					else if(response.errorCode==1)
						MessageService.showInfo('No Name details found',5000);
					else if(response.errorCode==-1)
						MessageService.showError(response.errorMsg,5000);
	       		},
		        function(errResponse){
	       			MessageService.showError(errResponse,5000);
		        }
		);
		
	}
	
	
});
