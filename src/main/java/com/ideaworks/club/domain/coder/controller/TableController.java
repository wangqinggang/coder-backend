package com.ideaworks.club.domain.coder.controller;

import com.ideaworks.club.domain.coder.domain.EntityDTO;
import com.ideaworks.club.domain.coder.domain.EntityField;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/table")
public class TableController {
//    @Autowired
//    private JdbcTemplate dao;



    @GetMapping("/entityDTO")
    public EntityDTO getEntityDTO(@RequestParam String tableName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        dataSource.setUrl("jdbc:jtds:sybase://129.1.50.194:8888/escloud_gy;charset=cp936");
        dataSource.setUsername("");
        dataSource.setPassword("");

        JdbcTemplate dao = new JdbcTemplate(dataSource);

        String sql = "select BH, TID, TNAME from ESTTABLE where TID =?";
        Map<String, Object> map = dao.queryForMap(sql, tableName);
        EntityDTO entityDTO = new EntityDTO();
        Integer bh1;
        if (!map.isEmpty()) {
            bh1 = MapUtils.getInteger(map, "BH");

            String sql1 = "select BH, TID, TNAME from ESTTABLE where BH =?";
            Map<String, Object> map1 = dao.queryForMap(sql1, bh1);

            if (!map1.isEmpty()) {
                Integer bh = MapUtils.getInteger(map1, "BH");
                String tid = MapUtils.getString(map1, "TID");
                String tname = MapUtils.getString(map1, "TNAME");
                entityDTO.setEntityName(tid.toLowerCase())
                        .setTableName(tid)
                        .setComment(tname)
                        .setApiTag(tname)
                        .setEntityFields(getColumns(bh1));
            }
        }
        return entityDTO;
    }

    @GetMapping("/columns")
    public List<EntityField> getColumns(@RequestParam Integer bh1) {
        List<EntityField> columnDTOList = getEntityFields(bh1);
        return columnDTOList;
    }

    /**
     * 根据编号获取字段列表
     *
     * @param bh1
     * @return
     */
    private List<EntityField> getEntityFields(Integer bh1) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        dataSource.setUrl("jdbc:jtds:sybase://129.1.50.194:8888/escloud_gy;charset=cp936");
        dataSource.setUsername("");
        dataSource.setPassword("");

        JdbcTemplate dao = new JdbcTemplate(dataSource);
        String sql = "select BH, COLID, COLNAME,COLTYPE,COLLENGTH,COLPK from ESTCOLUMN where BH =? order by COLXH";

        List<Map<String, Object>> maps = dao.queryForList(sql, bh1);

        List<EntityField> columnDTOList = new ArrayList<>();

        if (maps.size() > 0) {
            for (Map<String, Object> map : maps) {
                if (!map.isEmpty()) {
                    EntityField entityField = new EntityField();
//                    Integer bh = MapUtils.getInteger(map, "BH");
                    String colid = MapUtils.getString(map, "COLID");
                    String colname = MapUtils.getString(map, "COLNAME");
                    String coltype = MapUtils.getString(map, "COLTYPE");
                    Integer columnlength = MapUtils.getInteger(map, "COLLENGTH");
                    String colpk = MapUtils.getString(map, "COLPK");

                    entityField.setFieldName(colid.toLowerCase())
                            .setTableFieldName(colid)
                            .setFieldComment(colname)
                            .setLength(columnlength)
                            .setIsKey(colpk.equals("1"))
                            .setIsRequired(colpk.equals("1"))
                            .setDataType(get(coltype));
                    columnDTOList.add(entityField);
                }
            }
        }
        return columnDTOList;
    }

    /**
     * 根据coltype 获取数据类型
     *
     * @param coltype coltype 字典项
     * @return String 类型的 DataType
     */
    private String get(String coltype) {
        switch (coltype) {
            case "01":
                return "Byte";
            case "02":
                return "String";
            case "03":
                return "Date";
            case "04":
                return "Long";
            case "05":
                return "Float";
            case "06":
                return "Byte";
            case "07":
                return "Integer";
            case "08":
//                return "BigDecimal";
                return "Double";
            case "09":
                return "String";
            case "10":
                return "Long";
            case "11":
                return "String";
            case "12":
                return "Long";
            case "13":
                return "Date";
            case "14":
                return "Integer";
            case "15":
//                return "BigDecimal";
                return "Double";
            case "16":
                return "String";
            case "17":
                return "Integer";
            case "18":
                return "Byte";
            case "19":
                return "String";
            case "20":
                return "Long";
            default:
                return "String";

        }

    }

}
