/************************************************************************************************************
 * Class Name : DashBoardController.java 
 * Description: This controller is responsible for serving all the Dashboard REST calls. 
 * Author : Pankaj Chaudhary 
 * Date : July 31, 2016
 * ********************************************************************************************************** 
 * Description: Adding dynamic graph and static graphs for regular orders 
 * Author : Ankita
 * Mishra Date : April 04, 2017 
 * **********************************************************************************************************
 */

package com.zig.pso.rest.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zig.pso.logging.PSOLoggerSrv;
import com.zig.pso.rest.bean.DynamicGraphRequestBean;
import com.zig.pso.rest.bean.RegularOrdersCount;
import com.zig.pso.rest.bean.RegularOrdersCountList;
import com.zig.pso.rest.bean.StuckOrderBacklogUiResponseBean;
import com.zig.pso.rest.bean.StuckOrderDetailResponse;
import com.zig.pso.rest.bean.StuckOrdersCount;
import com.zig.pso.service.DashboardService;

@RestController
public class DashBoardController
{
    static final Logger logger = Logger.getLogger(DashBoardController.class);
    
    @Autowired
    DashboardService dashboardService;

    /*
     * This REST provides count of Stuck orders
     */
    @RequestMapping(value = "/stuckOrderCount", method = RequestMethod.GET)
    public ResponseEntity<StuckOrderDetailResponse> getStuckOrdersCount()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getStuckOrdersCount", "");

        StuckOrderDetailResponse stuckOrdersList = new StuckOrderDetailResponse();

        List<StuckOrdersCount> stuckOrderCountList = dashboardService.getStuckOrderList();
        stuckOrdersList.setStuckOrderList(stuckOrderCountList);
        return new ResponseEntity<StuckOrderDetailResponse>(stuckOrdersList, HttpStatus.OK);
    }

    @RequestMapping(value = "/stuckOrderList/{status}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getStuckOrders(@PathVariable("status") String status)
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getStuckOrders", "status : " + status);

        List<String> stuckOrderCountList = dashboardService.getStuckOrders(status);
        return new ResponseEntity<List<String>>(stuckOrderCountList, HttpStatus.OK);
    }

    @RequestMapping(value = "/stuckOrderHandled", method = RequestMethod.GET)
    public ResponseEntity<StuckOrderDetailResponse> getStuckOrderhandled()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getStuckOrderhandled", "");

        StuckOrderDetailResponse stuckOrdersList = new StuckOrderDetailResponse();

        List<StuckOrdersCount> stuckOrderCountList = dashboardService.getStuckOrderhandled();
        stuckOrdersList.setStuckOrderList(stuckOrderCountList);
        return new ResponseEntity<StuckOrderDetailResponse>(stuckOrdersList, HttpStatus.OK);
    }

    @RequestMapping(value = "/stuckOrderallStatus", method = RequestMethod.GET)
    public ResponseEntity<StuckOrderDetailResponse> getStuckOrderallStatus()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getStuckOrderallStatus", "");

        StuckOrderDetailResponse stuckOrdersList = new StuckOrderDetailResponse();

        List<StuckOrdersCount> stuckOrderCountList = dashboardService.getStuckOrderallStatus();
        stuckOrdersList.setStuckOrderList(stuckOrderCountList);
        return new ResponseEntity<StuckOrderDetailResponse>(stuckOrdersList, HttpStatus.OK);
    }

    @RequestMapping(value = "/stuckOrderBacklogs", method = RequestMethod.GET)
    public ResponseEntity<StuckOrderBacklogUiResponseBean> getStuckOrderBacklogList()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getStuckOrderallStatus", "");

        StuckOrderBacklogUiResponseBean data = dashboardService.getStuckOrderBacklogData();
        return new ResponseEntity<StuckOrderBacklogUiResponseBean>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/regularOrderList1", method = RequestMethod.GET)
    public ResponseEntity<RegularOrdersCountList> getRegularOrderList()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getRegularOrderList", "");

        RegularOrdersCountList regularOrdersCountList = new RegularOrdersCountList();

        List<RegularOrdersCount> regularOrderList = dashboardService.getRegularOrderListData();
        regularOrdersCountList.setRegularOrderList(regularOrderList);
        return new ResponseEntity<RegularOrdersCountList>(regularOrdersCountList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/regularOrderList", method = RequestMethod.GET)
    public ResponseEntity<RegularOrdersCountList> getRegularOrderList1()
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getRegularOrderList", "");
        RegularOrdersCountList regularOrdersCountList = new RegularOrdersCountList();
        List<RegularOrdersCount> regularOrderList = dashboardService.getRegOrderStatistics();
        regularOrdersCountList.setRegularOrderList(regularOrderList);
        return new ResponseEntity<RegularOrdersCountList>(regularOrdersCountList, HttpStatus.OK);
    }

    @RequestMapping(value = "/dynamicRegOrderList", method = RequestMethod.POST)
    public ResponseEntity<RegularOrdersCountList> getDynamicOrderList(@RequestBody DynamicGraphRequestBean dynamicGraphRequest)
    {
        PSOLoggerSrv.printDEBUG(logger, "DashBoardController", "getDynamicOrderList", "");

        RegularOrdersCountList dynamicOrdersCountList = new RegularOrdersCountList();

        dynamicOrdersCountList = dashboardService.getDynamicOrderListData(dynamicGraphRequest);
        return new ResponseEntity<RegularOrdersCountList>(dynamicOrdersCountList, HttpStatus.OK);
    }

}
