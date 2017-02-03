import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Selector;

/**
 * Created by iClaod on 10/20/16.
 */
public class JsonTest {

    public static void main(String[] args) {
        Json json = new Json("{\"listNews\":[{\"id\":\"43697\",\"news_id\":\"43697\",\"title\":\"餐饮人必知，通过“任务游戏”提升用户参与度的3大维度！\",\"type_id\":\"250\",\"typename\":\"\",\"clicks\":\"218\",\"description\":\"如今餐厅主流消费群趋向年轻化，80 后、90 后、00 后的年轻人具有很强的猎奇心理，诸如竞技类、冒险类、人物类等有趣的游戏往往更能博得他们的青睐。\",\"tag\":\"餐饮人<\\/a><\\/span>小猪猪餐厅<\\/a><\\/span>\",\"thumb\":\"http:\\/\\/www.canyin88.com\\/uploads\\/161020\\/139054-161020104K2N8.png\",\"ptime\":\"1小时前\",\"url\":\"\\/baodian\\/2016\\/10\\/20\\/43697.html\"},{\"id\":\"43696\",\"news_id\":\"43696\",\"title\":\"你不知道的购物商场里餐饮的秘密\",\"type_id\":\"249\",\"typename\":\"\",\"clicks\":\"222\",\"description\":\"商场对人气餐饮的迫切需要、新商场与人气餐饮新店数量的失衡、餐饮对选址的高要求等都让开发商越来越倒贴人气餐饮。\",\"tag\":\"商场餐饮<\\/a><\\/span>购物中心餐饮<\\/a><\\/span>\",\"thumb\":\"http:\\/\\/www.canyin88.com\\/uploads\\/161020\\/139054-16102010321M40.jpg\",\"ptime\":\"1小时前\",\"url\":\"\\/zixun\\/2016\\/10\\/20\\/43696.html\"},{\"id\":\"43694\",\"news_id\":\"43694\",\"title\":\"餐饮大牌巧妙的定价策略，你get了吗？\",\"type_id\":\"250\",\"typename\":\"\",\"clicks\":\"285\",\"description\":\"餐品定价是门大学问。一般来说，在确定餐品定价时，要考虑以下几个因素：原料价格（主料+辅料+调料）、同行定价、餐厅的特色及服务等。\",\"tag\":\"菜品定价<\\/a><\\/span>菜单设计<\\/a><\\/span>\",\"thumb\":\"http:\\/\\/www.canyin88.com\\/uploads\\/161020\\/139054-16102010052D17.jpg\",\"ptime\":\"1小时前\",\"url\":\"\\/baodian\\/2016\\/10\\/20\\/43694.html\"},{\"id\":\"43693\",\"news_id\":\"43693\",\"title\":\"成名600年的烤鸭鼻祖，竟这样被全聚德抢了风头？\",\"type_id\":\"249\",\"typename\":\"\",\"clicks\":\"464\",\"description\":\"在全聚德出现之前，有一家烤鸭店来到这世上已经有348年了，而且生意很不错。面对烤鸭市场的龙头老大，小辈儿的全聚德是模仿还是超越？\",\"tag\":\"全聚德<\\/a><\\/span>烤鸭鼻祖<\\/a><\\/span>\",\"thumb\":\"http:\\/\\/www.canyin88.com\\/uploads\\/161020\\/139054-161020093P2P0.jpg\",\"ptime\":\"2小时前\",\"url\":\"\\/zixun\\/2016\\/10\\/20\\/43693.html\"},{\"id\":\"43692\",\"news_id\":\"43692\",\"title\":\"好吃的路边摊离我们的“安全”食品差了多少个饿了么？\",\"type_id\":\"249\",\"typename\":\"\",\"clicks\":\"462\",\"description\":\"近来，有一个消息在网络与朋友圈引起了轩然大波，什么消息呢？就是各位吃货万众瞩目的阿大葱油饼终于重出江湖。一家葱油饼为什么这么有名？\",\"tag\":\"阿大葱油饼<\\/a><\\/span>饿了么<\\/a><\\/span>\",\"thumb\":\"http:\\/\\/www.canyin88.com\\/uploads\\/161020\\/139054-161020092Q5934.png\",\"ptime\":\"2小时前\",\"url\":\"\\/zixun\\/2016\\/10\\/20\\/43692.html\"}],\"nextPageUrl\":\"\\/plus\\/ajaxindex.php?cpage=1&psize=5&other=index\",\"para\":\"true\"}");
        Selectable result = json.jsonPath("$.listNews");
        int i=0;
        for (Selectable selectable: result.nodes()) {
            i++;
            System.out.println(i + ": ");
            System.out.println(new Json(selectable.get()).jsonPath("$.thumb"));
            System.out.println(new Json(selectable.get()).jsonPath("$.url"));
        }


    }

}
