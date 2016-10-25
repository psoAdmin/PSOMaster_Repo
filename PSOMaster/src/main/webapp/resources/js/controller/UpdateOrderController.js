module.controller("UpdateOrderController", function($scope, $routeParams,$http,FileUploadService,MessageService,UpdateOrderService) {
	
	$scope.title = "Update Order";
	
	$scope.updateType="status";
	
	$scope.orderID = $routeParams.orderID;
	
	$scope.openHelpModel = function(){
        $("#UpdateHelp-modal").modal();
	}
	
	
	$scope.updateOrderRes = {};
	$scope.updateOrder = function(updatetype){
		
		var newValue='';
		
		if(updatetype=='status')
			newValue=$scope.newStatus;
		else if(updatetype=='sim')
			newValue=$scope.newSIM;
		else if(updatetype=='imei')
			newValue=$scope.newIMEI;
		else if(updatetype=='retry')
			newValue=$scope.newRetry;
			
		var orderID = $scope.orderID;
		
		UpdateOrderService.updateOrderDetails(updatetype,newValue,orderID).then(
				function(response) {
					if(response!=undefined && response.errorCode == 0){
						$scope.updateOrderRes = response;
						MessageService.showSuccess(response.errorMsg,5000);
					}
					else{
						var errorMessage = response.errorMsg + "\n Log Reference ID : " + response.logRefId;
						MessageService.showError(errorMessage,null);
					}
	       		},
		       function(errResponse){
					MessageService.showSuccess('Error occured while updating order. Please try again later.',5000);
		       }
		);
       
	}
	
	
	 $scope.uploadFile = function(){
		 var updateType = $scope.updateType;
		 if(undefined == $scope.myFile){
			 MessageService.showError('Please upload File to update Orders',5000);
			 return;
		 }
		 var file = $scope.myFile;
		 var uploadUrl = "upload/"+$scope.updateType;
	      
	     updateBulkOrder(file,uploadUrl);
	 };
	 
	 
	 function updateBulkOrder(file,uploadUrl){
	      
	      FileUploadService.uploadFileToUrl(file, uploadUrl).then(
				function(response) {
					if(response.errorCode == 0){
						MessageService.showSuccess(response.errorMsg,5000);
					}
					else if(response.errorCode == 1){
						MessageService.showInfo(response.errorMsg,5000);
					}
					else if(response.errorCode == -1){
						var errorMessage = response.errorMsg + "\n Log Reference ID : " + response.logRefId;
						MessageService.showError(errorMessage,null);
					}
					
					if(response.invalidOrders.length>0){
						$scope.inValidOrders = response.invalidOrders;
						$("#UpdateResponse-modal").modal();
					}
	       		},
		        function(errResponse){
	       			MessageService.showError(errResponse,5000);
		        }
		  );
	 }
	 
});