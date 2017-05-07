/************************************************************************************************************
 * Class Name : UpdateOrderManagerDAOImpl.java 
 * Description: 
 * Author : Ankita Mishra 
 * Date : Aug 1, 2016 
 * **********************************************************************************************************
 */
package com.zig.pso.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zig.pso.constants.PSOConstants;
import com.zig.pso.logging.PSOLoggerSrv;
import com.zig.pso.rest.bean.BaseResponseBean;
import com.zig.pso.rest.bean.ColumnDataListBean;
import com.zig.pso.rest.bean.OrderUpdateInputData;
import com.zig.pso.rest.bean.TempInsertBUResponse;
import com.zig.pso.rest.bean.UpdateMultiOrderDetailsRequestBean;
import com.zig.pso.rest.bean.UpdateOrderRequestBean;
import com.zig.pso.utility.CommonUtility;
import com.zig.pso.utility.DBConnection;
import com.zig.pso.utility.OrderQueries;

/**
 * 
 */
@Repository
public class UpdateOrderManagerDAOImpl implements UpdateOrderManagerDAO
{
    
    @Autowired
    OrderInfoManagerDAO orderDAO;

    /**
     * @param portalDBConnection
     */
    public UpdateOrderManagerDAOImpl()
    {
        super();
    }

    private Connection getPortalDbConnction()
    {
        return DBConnection.getPortalDBConnection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateOrderStatus(com.zig.pso.rest .bean.UpdateOrderRequestBean)
     */
    @Override
    public BaseResponseBean updateOrderStatus(UpdateOrderRequestBean updateOrderRequest)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderStatus();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            
            String currentStatus = orderDAO.getOrderCurrentValue(updateOrderRequest.getOrderId(), updateOrderRequest.getLineId(), updateOrderRequest.getType());
            
            pstm = con.prepareStatement(sql);
            pstm.setString(1, updateOrderRequest.getNewValue());
            pstm.setString(2, updateOrderRequest.getOrderId());
            int i = pstm.executeUpdate();
            if (i < 1)
            {
                updateOrderRes.setErrorCode(PSOConstants.INFO_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.NO_ORDER_UPDATED);
                updateOrderRes.setLogRefId(logRefID);
                
            }
            else
            {
                updateOrderRes = updateOrderTrack(updateOrderRequest, "ZIG_AUTO_MASTER",logRefID,currentStatus);
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.ORDER_UPDATE_SUCCESSFULL);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateOrderStatus", logRefID, sql,updateOrderRequest, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateOrderStatus", logRefID, sql,updateOrderRequest, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderStatus", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderStatus", e);
                }
            }
        }

        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateOrderSim(com.zig.pso.rest .bean.UpdateOrderRequestBean)
     */
    @Override
    public BaseResponseBean updateOrderSim(UpdateOrderRequestBean updateOrderRequest)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderSim();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            String currentSIMValue = orderDAO.getOrderCurrentValue(updateOrderRequest.getOrderId(), updateOrderRequest.getLineId(), updateOrderRequest.getType());
            
            pstm = con.prepareStatement(sql);
            pstm.setString(1, updateOrderRequest.getNewValue());
            pstm.setString(2, updateOrderRequest.getOrderId());
            pstm.setString(3, updateOrderRequest.getLineId());
            int i = pstm.executeUpdate();
            if (i < 1)
            {
                updateOrderRes.setErrorCode(PSOConstants.INFO_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.NO_ORDER_UPDATED);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes = updateOrderTrack(updateOrderRequest, "ZIG_ORDER_SHIPMENT_INFO",logRefID,currentSIMValue);
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.ORDER_UPDATE_SUCCESSFULL);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateOrderSim", logRefID, sql,updateOrderRequest, updateOrderRes.getErrorMsg());
            
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateOrderSim", logRefID, sql,updateOrderRequest, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderSim", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderSim", e);
                }
            }
        }
        
        return updateOrderRes;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateOrderImei(com.zig.pso.rest .bean.UpdateOrderRequestBean)
     */
    @Override
    public BaseResponseBean updateOrderImei(UpdateOrderRequestBean updateOrderRequest)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderIMEI();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            String currentIMEIValue = orderDAO.getOrderCurrentValue(updateOrderRequest.getOrderId(), updateOrderRequest.getLineId(), updateOrderRequest.getType());
            
            pstm = con.prepareStatement(sql);
            pstm.setString(1, updateOrderRequest.getNewValue());
            pstm.setString(2, updateOrderRequest.getOrderId());
            pstm.setString(3, updateOrderRequest.getLineId());
            int i = pstm.executeUpdate();
            if (i < 1)
            {
                updateOrderRes.setErrorCode(PSOConstants.INFO_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.NO_ORDER_UPDATED);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes = updateOrderTrack(updateOrderRequest, "ZIG_ORDER_SHIPMENT_INFO",logRefID,currentIMEIValue);
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.ORDER_UPDATE_SUCCESSFULL);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateOrderImei", logRefID,sql,updateOrderRequest, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateOrderImei", logRefID, sql,updateOrderRequest, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderImei", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderImei", e);
                }
            }
        }
        
        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateOrderRetryCount(com.zig.pso .rest.bean.UpdateOrderRequestBean)
     */
    @Override
    public BaseResponseBean updateOrderRetryCount(UpdateOrderRequestBean updateOrderRequest)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderRetryCount();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            String currentRetryCount = orderDAO.getOrderCurrentValue(updateOrderRequest.getOrderId(), updateOrderRequest.getLineId(), updateOrderRequest.getType());
            
            pstm = con.prepareStatement(sql);
            pstm.setString(1, updateOrderRequest.getNewValue());
            pstm.setString(2, updateOrderRequest.getOrderId());
            int i = pstm.executeUpdate();
            if (i < 1)
            {
                updateOrderRes.setErrorCode(PSOConstants.INFO_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.NO_ORDER_UPDATED);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes = updateOrderTrack(updateOrderRequest, "ZIG_AUTO_MASTER",logRefID,currentRetryCount);
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.ORDER_UPDATE_SUCCESSFULL);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateOrderRetryCount", logRefID,sql,updateOrderRequest, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateOrderRetryCount", logRefID, sql,updateOrderRequest, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderRetryCount", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderRetryCount", e);
                }
            }
        }
        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateOrderTrack(com.zig.pso.rest.bean.UpdateOrderRequestBean)
     */
    public BaseResponseBean updateOrderTrack(UpdateOrderRequestBean updateOrderRequest, String tablename, String logRefId,String currentValue)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String sql = OrderQueries.updateOrderTrack();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            pstm = con.prepareStatement(sql);
            pstm.setString(1, updateOrderRequest.getOrderId());
            pstm.setString(2, updateOrderRequest.getLineId());
            pstm.setString(3, updateOrderRequest.getType());
            pstm.setString(4, currentValue);
            pstm.setString(5, updateOrderRequest.getNewValue());
            pstm.setString(6, logRefId);
            pstm.setString(7, "admin");
            pstm.setString(8, tablename);
            int i = pstm.executeUpdate();
            if (i < 1)
            {
                updateOrderRes.setErrorCode(PSOConstants.INFO_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.NO_ORDER_INSERTED);
                updateOrderRes.setLogRefId(logRefId);
            }
            else
            {
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.ORDER_INSERT_SUCCESSFULL);
                updateOrderRes.setLogRefId(logRefId);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateOrderTrack", logRefId,sql,updateOrderRequest, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateOrderTrack", logRefId, sql,updateOrderRequest, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderTrack", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateOrderTrack", e);
                }
            }
        }
        
        
        return updateOrderRes;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateBulkOrderStatus(java.util.ArrayList)
     */
    @Override
    public BaseResponseBean updateBulkOrderStatus(ArrayList<OrderUpdateInputData> orderUpdateData)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderStatus();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            pstm = con.prepareStatement(sql);

            for (OrderUpdateInputData order : orderUpdateData)
            {
                pstm.setString(1, order.getStatus().trim());
                pstm.setString(2, order.getOrderId());
                pstm.addBatch();
            }

            int[] recordsUpdated = pstm.executeBatch();
            if (recordsUpdated.length < 0)
            {
                updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateBulkOrderStatus", logRefID,sql,orderUpdateData, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateBulkOrderStatus", logRefID, sql,orderUpdateData, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderStatus", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderStatus", e);
                }
            }
        }

        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateBulkOrderSim(java.util.ArrayList)
     */
    @Override
    public BaseResponseBean updateBulkOrderSim(ArrayList<OrderUpdateInputData> orderUpdateData)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderSim();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            pstm = con.prepareStatement(sql);

            for (OrderUpdateInputData order : orderUpdateData)
            {
                pstm.setString(1, order.getSim().trim());
                pstm.setString(2, order.getOrderId());
                pstm.addBatch();
            }

            int[] recordsUpdated = pstm.executeBatch();
            if (recordsUpdated.length < 0)
            {
                updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateBulkOrderSim", logRefID,sql,orderUpdateData, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateBulkOrderSim", logRefID, sql,orderUpdateData, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderSim", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderSim", e);
                }
            }
        }

        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateBulkOrderImei(java.util.ArrayList)
     */
    @Override
    public BaseResponseBean updateBulkOrderImei(ArrayList<OrderUpdateInputData> orderUpdateData)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderIMEI();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            
            pstm = con.prepareStatement(sql);

            for (OrderUpdateInputData order : orderUpdateData)
            {
                pstm.setString(1, order.getImei().trim());
                pstm.setString(2, order.getOrderId());
                pstm.addBatch();
            }

            int[] recordsUpdated = pstm.executeBatch();
            if (recordsUpdated.length < 0)
            {
                updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateBulkOrderImei", logRefID,sql,orderUpdateData, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateBulkOrderImei", logRefID, sql,orderUpdateData, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderImei", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderImei", e);
                }
            }
        }

        return updateOrderRes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateBulkOrderRetryCount(java.util.ArrayList)
     */
    @Override
    public BaseResponseBean updateBulkOrderRetryCount(ArrayList<OrderUpdateInputData> orderUpdateData)
    {
        BaseResponseBean updateOrderRes = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.updateOrderRetryCount();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            
            pstm = con.prepareStatement(sql);

            for (OrderUpdateInputData order : orderUpdateData)
            {
                pstm.setString(1, order.getRetryCount().trim());
                pstm.setString(2, order.getOrderId());
                pstm.addBatch();
            }

            int[] recordsUpdated = pstm.executeBatch();
            if (recordsUpdated.length < 0)
            {
                updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
                updateOrderRes.setLogRefId(logRefID);
            }
            else
            {
                updateOrderRes.setErrorCode(PSOConstants.SUCCESS_CODE);
                updateOrderRes.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
                updateOrderRes.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateBulkOrderRetryCount", logRefID,sql,orderUpdateData, updateOrderRes.getErrorMsg());
        }
        catch (SQLException e)
        {
            updateOrderRes.setErrorCode(PSOConstants.ERROR_CODE);
            updateOrderRes.setErrorMsg(PSOConstants.BACKEND_ERROR);
            updateOrderRes.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateBulkOrderRetryCount", logRefID, sql,orderUpdateData, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderRetryCount", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderRetryCount", e);
                }
            }
        }

        return updateOrderRes;
    }
    
    public BaseResponseBean createTempTableForBulkUpdate(String tempTableName)
    {
        BaseResponseBean createTempTableResp = new BaseResponseBean();
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = OrderQueries.getCreateTempTableForBulkUpdateSQL();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            sql = sql.replace("<TABLE_NAME>", tempTableName);
            
            pstm = con.prepareStatement(sql);
            pstm.execute();
            
            createTempTableResp.setErrorCode(PSOConstants.SUCCESS_CODE);
            createTempTableResp.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
            createTempTableResp.setLogRefId(logRefID);
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "createTempTableForBulkUpdate", logRefID,sql,"Creating Temp table for Bulk update", createTempTableResp.getErrorMsg());
        }
        catch (SQLException e)
        {
            createTempTableResp.setErrorCode(PSOConstants.ERROR_CODE);
            createTempTableResp.setErrorMsg(PSOConstants.BACKEND_ERROR);
            createTempTableResp.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "createTempTableForBulkUpdate", logRefID, sql,"Creating Temp table for Bulk update", e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "createTempTableForBulkUpdate", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "createTempTableForBulkUpdate", e);
                }
            }
        }

        return createTempTableResp;
    }

    /* (non-Javadoc)
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#insertBulkOrderDataInTempTable(java.util.ArrayList)
     */
    @Override
    public TempInsertBUResponse insertBulkOrderDataInTempTable(ArrayList<OrderUpdateInputData> orderUpdateData, String updateType)
    {
        TempInsertBUResponse insertDataInTempTableResp = new TempInsertBUResponse();
        String logRefID = CommonUtility.getLogRefID();
        
        String bulkUpdateID = CommonUtility.getRandomBulkUpdateID();
        
        String sql = OrderQueries.getInsertBulkOrderInTempTableSQL();
        
        PreparedStatement pstm = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            pstm = con.prepareStatement(sql);

            for (OrderUpdateInputData order : orderUpdateData)
            {
                pstm.setString(1, bulkUpdateID);
                pstm.setString(2, order.getOrderId());
                pstm.setLong(3, Long.parseLong((order.getLineId()!=null)?order.getLineId():"0"));
                pstm.setString(4, order.getSim());
                pstm.setString(5, order.getImei());
                pstm.setString(6, order.getStatus());
                pstm.setString(7, order.getRetryCount());
                pstm.setString(8, PSOConstants.PSO_ADMIN_USER);
                pstm.addBatch();
            }

            int[] recordsInserted = pstm.executeBatch();
            if (recordsInserted.length < 0)
            {
                insertDataInTempTableResp.setErrorCode(PSOConstants.ERROR_CODE);
                insertDataInTempTableResp.setErrorMsg(PSOConstants.BACKEND_ERROR);
                insertDataInTempTableResp.setLogRefId(logRefID);
            }
            else
            {
                insertDataInTempTableResp.setErrorCode(PSOConstants.SUCCESS_CODE);
                insertDataInTempTableResp.setErrorMsg(PSOConstants.BULK_TEMP_INSERT_SUCCESS);
                insertDataInTempTableResp.setLogRefId(logRefID);
            }
            
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "insertBulkOrderDataInTempTable", logRefID,sql,orderUpdateData, insertDataInTempTableResp.getErrorMsg());
        }
        catch (SQLException e)
        {
            insertDataInTempTableResp.setErrorCode(PSOConstants.ERROR_CODE);
            insertDataInTempTableResp.setErrorMsg(PSOConstants.BACKEND_ERROR);
            insertDataInTempTableResp.setLogRefId(logRefID);
            
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "insertBulkOrderDataInTempTable", logRefID, sql,orderUpdateData, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "insertBulkOrderDataInTempTable", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "insertBulkOrderDataInTempTable", e);
                }
            }
        }
        
        insertDataInTempTableResp.setBulkUpdateId(bulkUpdateID);
        return insertDataInTempTableResp;
    }

    /* (non-Javadoc)
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#getBulkOrderDataFromTempTable(java.lang.String)
     */
    @Override
    public ArrayList<OrderUpdateInputData> getBulkOrderDataFromTempTable(String bulkUpdateId)
    {
        ArrayList<OrderUpdateInputData> tempTableDataList = new ArrayList<OrderUpdateInputData>();
        OrderUpdateInputData orderData = null;
        
        String sql = OrderQueries.getReadTempTableforBulkOrderSQL();
        
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            pstm = con.prepareStatement(sql);
            pstm.setString(1, bulkUpdateId);
            rs = pstm.executeQuery();
            while (rs.next())
            {
                orderData = new OrderUpdateInputData();
                orderData.setImei(rs.getString("IMEI"));
                orderData.setLineId(rs.getString("LINE_ID"));
                orderData.setOrderId(rs.getString("ORDER_ID"));
                orderData.setRetryCount(rs.getString("RETRY"));
                orderData.setSim(rs.getString("SIM"));
                orderData.setStatus(rs.getString("STATUS_CODE"));
                orderData.setBulkUpdateId(rs.getString("BU_SES_ID"));
                tempTableDataList.add(orderData);
            }
        }
        catch (SQLException e)
        {
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "getBulkOrderDataFromTempTable", e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "getBulkOrderDataFromTempTable", e);
                }
            }
            if (pstm != null)
            {
                try
                {
                    pstm.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "getBulkOrderDataFromTempTable", e);
                }
            }
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "getBulkOrderDataFromTempTable", e);
                }
            }
        }
        
        return tempTableDataList;
    }

    /* (non-Javadoc)
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateMultiOrderDetails(com.zig.pso.rest.bean.UpdateMultiOrderDetailsRequestBean)
     */
    @Override
    public BaseResponseBean updateMultiOrderDetails(UpdateMultiOrderDetailsRequestBean updateOrderRequest)
    {
        String SQL = "UPDATE <TABLE_NAME> SET <UPDATE_COLUMN_DATA> WHERE <CONDITION_CLAUSE>";
        SQL = SQL.replace("<TABLE_NAME>", updateOrderRequest.getTabName());
        
        String aaa = "";
        for(ColumnDataListBean col : updateOrderRequest.getColData()){
            aaa = aaa + col.getColName() + " = '" + col.getColValue() + "', ";
        }
        String bbb = aaa;
        System.out.println(aaa);
        System.out.println(aaa.substring(0, aaa.length()-1));
        String finalcolData = bbb.substring(0, bbb.length()-1);
        System.out.println(finalcolData);
        SQL = SQL.replace("<UPDATE_COLUMN_DATA>", finalcolData);
        
        String aaa2 = "STATUS_CODE = 'PDRS', RETRY = '0',";
        System.out.println(aaa2.substring(0, aaa2.length()-1));
        
        String conditionData = " ORDER_ID = " + updateOrderRequest.getOrderId();
        SQL = SQL.replace("<CONDITION_CLAUSE>", conditionData);
        
        
        System.out.println(SQL);
        
        return null;
    }
    
    
    public static void main(String[] args)
    {
        String aaa = "STATUS_CODE = 'PDRS', RETRY = '0',";
        System.out.println(aaa.substring(0, aaa.length()-1));
    }

    /* (non-Javadoc)
     * @see com.zig.pso.dao.UpdateOrderManagerDAO#updateBulkOrderDetails(java.lang.String)
     */
    @Override
    public BaseResponseBean updateBulkOrderDetails(String bulkUpdateId)
    {
        BaseResponseBean response = new BaseResponseBean();
        
        String logRefID = CommonUtility.getLogRefID();
        
        String sql = "{call PORTAL_BULK_UPDATE(?,?,?,?,?)}";
        
        CallableStatement callableStatement = null;
        Connection con = this.getPortalDbConnction();
        
        try
        {
            callableStatement = con.prepareCall(sql);
            callableStatement.setString(1, bulkUpdateId);
            callableStatement.setString(2, "Admin");
            callableStatement.setString(3, logRefID);
            callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
            callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
            
            callableStatement.executeUpdate();
            
            String errorCode = callableStatement.getString(4);
            String errorMessage = callableStatement.getString(5);
            
            if(PSOConstants.BULK_UPDATE_SUCCESS_CODE.equals(errorCode)){
                response.setErrorCode(PSOConstants.SUCCESS_CODE);
                response.setErrorMsg(PSOConstants.BULK_UPDATE_SUCCESS);
            }
            else{
                response.setErrorCode(PSOConstants.ERROR_CODE);
                response.setErrorMsg(PSOConstants.BULK_UPDATE_FAILURE);
            }
            PSOLoggerSrv.printSQL_DEBUG("UpdateOrderManagerDAOImpl", "updateBulkOrderDetails", logRefID,sql,bulkUpdateId, errorMessage);
        }
        catch (SQLException e)
        {
            PSOLoggerSrv.printERROR("UpdateOrderManagerDAOImpl", "updateBulkOrderDetails", logRefID,sql,bulkUpdateId, e);
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderDetails", e);
                }
            }
            if (callableStatement != null)
            {
                try
                {
                    callableStatement.close();
                }
                catch (SQLException e)
                {
                    PSOLoggerSrv.printERROR("DashboardDAOImpl", "updateBulkOrderDetails", e);
                }
            }
        }
        
        return response;
    }

}
