package com.tochange.yang.lib.utils;

import java.util.Map;

/**
 * 文件操作过程中的回调接口，传回被操作文件的具体信息(如文件名，修改时间等)
 */
public interface IFileOperateCallback
{
    /**
     * 由文件操作调用者实现，对被操作文件的具体信息进行处理
     * 
     * @param Map Key描述： srcfilename：原始文件名称 lastmodifyed：最后修改时间
     * @return 操作是否成功的标识
     */
    public boolean operate(Map<String, String> parma);
}
