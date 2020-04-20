# boot-lettuce
该项目为springBoot整合redisTemplate(Lettuce)
优化redisTemplate
- 问题1:如果存的是hash类型的话对于不同的对象就要填入不同的字符串，像下面的情形就需要写很多重复的 key 的值
```java
redisTemplate.opsForHash().get("user",id);
redisTemplate.opsForHash().put("user",id,u);
```
解决方案1：声明一个工具类定义各种name
```java
public interface KeyNameUtil {
  String USER = "user";
  Stromg STUDENT = "student";
}
```
使用
```java
redisTemplate.opsForHash().get(KeyNameUtil.USER,id);
redisTemplate.opsForHash().put(KeyNameUtil.STUDENT,id,s);
```
解决方案2: 在实体bean声明里声明一个方法获取name
```java
public class User implements Serializable {
  public static String getKeyName(){ //static 关键字就是只会执行一次，不会重复执行，而且可以通过类名访问
    return "user";
  }
}
public class Student implements Serializable {
  public static String getKeyName(){
    return "student";
  }
}
```
使用
````java
redisTemplate.opsForHash().get(User.getKeyName(),id);
redisTemplate.opsForHash().put(Student.getKeyName(),id,s);
```
- 问题2:强制类型转换问题
- 问题3:redisTemplate.opsForHash(),每次都写这样一长串
答：因为redisTemplate.opsForXXX()返回类型都是ValueOperations<K,V>，通过注入操作类这个bean并指定范型来操作
```
//    ValueOperations<String, String> string = redisTemplate.opsForValue();
    @Resource(name = "redisTemplate") //跟 配置文件定义的 redisTemplate方法 名字一样，因为@Resource是按名字搜索装配的
    private ValueOperations<String, String> string;
```
