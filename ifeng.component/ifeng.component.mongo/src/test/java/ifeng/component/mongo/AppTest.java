package ifeng.component.mongo;

import com.ifeng.core.data.ILoader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        MongoTest();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void MongoTest(){
        Entity en = new Entity();
        en.setName("name");
        en.setValue("ddd");
        Inner in = new Inner();
        in.setType("asd");
        en.getList().add(in);

        in = new Inner();
        in.setType("asd11");
        en.getList().add(in);

        en.encode();
    }
    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    class Inner extends MongoCodec{
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public void decode(ILoader loader) {

        }
    }
    class Entity extends MongoCodec{

        private String name;
        private String value;
        private List<Inner> list = new ArrayList<>();

        public List<Inner> getList() {
            return list;
        }

        public void setList(List<Inner> list) {
            this.list = list;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public void decode(ILoader loader) {

        }
    }
}
