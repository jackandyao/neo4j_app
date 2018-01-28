package com.graphdb.neo4j;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Administrator on 10/15.
 */
public class Match2D3 {

    Driver driver;

    public Match2D3(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    //界面传回操作请求，拼成Match语句查库，查库结果拼成json格式写json文件
    public void gernerateJsonFile()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        StatementResult result = session.run(
                "MATCH p=(n:People)-[]-() RETURN p");

        StringBuffer nodes = new StringBuffer();
        StringBuffer links = new StringBuffer();
        nodes.append("\"nodes\":[");
        links.append("\"links\":[");

        while (result.hasNext())
        {
            Record record = result.next();
            System.out.println(record);
            List<Value> list = record.values();
            for(Value v : list)
            {
                Path p = v.asPath();
                for(Node n:p.nodes())
                {
//                    System.out.println(n.labels());
                    nodes.append("{");
//                    System.out.println(n.size());
                    int num = 0;
                    for(String k:n.keys())
                    {
//                        System.out.println(k+"-"+n.get(k));
                        nodes.append("\""+k+"\":"+n.get(k)+",");
                        num ++ ;
                        if(num == n.size())
                        {
                            nodes.append("\"id\":"+n.id());
                        }
                    }

                    nodes.append("},");
                }
                nodes=new StringBuffer(nodes.toString().substring(0,nodes.toString().length()-1));
//                System.out.println(p);

                for(Relationship r:p.relationships())
                {
//                    System.out.println(n.labels());
                    links.append("{");
                    System.out.println(r);
                    int num = 0;
                    links.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links.append(",\"type\":\""+r.type()+"\"");
                    links.append("},");
                }
                links=new StringBuffer(links.toString().substring(0,links.toString().length()-1));

            }
            nodes.append(",");
            links.append(",");

        }
        nodes=new StringBuffer(nodes.toString().substring(0,nodes.toString().length()-1));
        links=new StringBuffer(links.toString().substring(0,links.toString().length()-1));

        nodes.append("]");
        links.append("]");
        System.out.println(nodes.toString());
        System.out.println(links.toString());
        String resultJson = "{"+nodes+","+links+"}";
//        System.out.println(resultJson);
//        System.out.println(nodes.toString());


        try {
            FileOutputStream fos = new FileOutputStream("webapp/people.json");
            fos.write(resultJson.getBytes());
            fos.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static void main(String... args)
    {
        Match2D3 example = new Match2D3("bolt://localhost:7687", "neo4j", "123456");
        example.gernerateJsonFile();

    }

}
