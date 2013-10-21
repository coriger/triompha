package com.triompha.common;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

/******
 * 一致性hash算法的简单的实现
 * 算法的均匀度在一定程度上取决于 hash算法的均匀度
 * 
 * 一致性hash算法的缺陷，可能存在数据不一致性
 * 1，当服务器A下线，那么原打到这个服务器的 将被均衡打到其他服务器，如果A服务器好了再次上线，那么将再次打到这个服务器，如果A再次下线就将导致数据不一致
 * 2，当服务器A下线（由于网络因素，而A服务器的数据并未被清除），那么原打到这个服务器的 将被均衡打到其他服务器，如果A再次上线就将导致数据的不一致
 * 
 * 
 * 解决方案 1，放的缓存数据不能是永久有效的，最好1，2天的有效期 （不能完全解决），完全解决的方法是，绝不随意增减服务器数量，当一台服务器下线的时候拿另一台服务器进行顶起
 *           并使用特定的名称代表ip+端口
 *           如  server1 = 1.1.1.1：11211 back=1.1.1.3:11211
 *               server2 = 1.1.1.2:11212
 *               
 *        失败转移是转移到备机，并重启主机，当换回主机的时候重启备机  
 *        而不是自动重新将这台机器的数据进行重hash     
 *           
 *         2，所有当决定下线一台服务器的时候一定要将数据清空，或重启
 * 
 * 
 * @author triompha
 *
 * @param <T>
 */
public class ConsistentHash<T> {  
  
       private final HashFunction hashFunction;  
       private final int numberOfReplicas;  
       private final SortedMap<Integer, T> circle = new TreeMap<Integer,T>();  
  
       public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {  
             this .hashFunction = hashFunction;  
             this .numberOfReplicas = numberOfReplicas;  
  
             for (T node : nodes) {  
                  add(node);  
            }  
      }  
  
       public void add(T node) {  
             for (int i = 0; i < numberOfReplicas; i++) {  
                   circle .put(hashFunction .hash(node.toString() + i), node);  
            }  
      }  
  
       public void remove(T node) {  
             for (int i = 0; i < numberOfReplicas; i++) {  
                   circle .remove(hashFunction .hash(node.toString() + i));  
            }  
      }  
  
       public T get(Object key) {  
             if (circle .isEmpty()) {  
                   return null ;  
            }  
             int hash = hashFunction .hash(key);  
             if (!circle .containsKey(hash)) {  
                  SortedMap<Integer, T> tailMap = circle .tailMap(hash);  
                  hash = tailMap.isEmpty() ? circle .firstKey() : tailMap.firstKey();  
            }  
             return circle .get(hash);  
      }  
  
       static class HashFunction {  
             int hash(Object key) {
                   //md5加密后，hashcode
                   return DigestUtils.md5Hex(key.toString()).hashCode();  
            }  
      }  
  
  
}  


