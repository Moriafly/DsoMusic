package com.dirror.music.foyou.sentence.foyoulibrary

import com.dirror.foyou.sentence.SentenceData

object FoyouLiterature {
    fun getLiterature (): SentenceData {
        //val arrayStr
        val arrayStr: Array<SentenceData> = arrayOf(
            SentenceData("倘若我问心有愧呢？", "周芷若", "倚天屠龙记"),
            SentenceData("你想和别人制造牵绊，就得承受流泪的风险。", "安东尼·德·圣-埃克苏佩里", "小王子"),
            SentenceData("使沙漠显得美丽的，是它在什么地方藏着一口水井。", "安东尼·德·圣-埃克苏佩里", "小王子"),
            SentenceData("十年春，齐师伐我。", "", "曹刿论战"),
            SentenceData("合抱之木，生于毫末；九层之台，起于累土；千里之行，始于足下。", "老子", "道德经·第六十四章"),

            SentenceData("不爱其亲，岂能及物？", "", "陈书·虞荔"),
            SentenceData("及至始皇，奋六世之余烈，振长策而御宇内，吞二周而亡诸侯，履至尊而制六合，执敲扑而鞭笞天下，威振四海。", "贾谊", "过秦论"),
            SentenceData("人有逆天之时，天无绝人之路。", "", "醒世恒言"),
            SentenceData("文章，经国之大业，不朽之盛事。", "曹丕", "典论"),
            SentenceData("治国之道，必先富民。", "管子", "治国"),

            SentenceData("聚蚊成雷。", "", "汉书·传·景十三王传"),
            SentenceData("撒哈拉沙漠是这么的美丽，而这儿的生活却是要付出无比的毅力才能适应。", "三毛", "撒哈拉沙漠的故事"),
            SentenceData("永远不要相信苦难是值得的，苦难就是苦难，苦难不会带来成功。苦难不值得追求，磨炼意志是因为苦难无法躲开。", "余华", "活着"),
            SentenceData("朽株将拔，非待寻斧，落叶就殒，无劳烈风。", "", "陈书"),
            SentenceData("庄周有云：人情险于山川。", "", "后汉书"),

            SentenceData("失之东隅，收之桑榆。", "", "后汉书·列传·冯岑贾列传"),
            SentenceData("人恒过然后能改，困于心衡于虑而后作，征于色发于声而后喻。", "", "孟子"),
            SentenceData("斑竹半帘，唯道我心清似水；黄粱一梦，任他世事冷如冰。", "陈继儒", "后汉书"),
            SentenceData("我知言，我善养吾浩然之气。", "孔子", "论语"),
            SentenceData("我怕君心易变，浮生若梦。", "", "游园惊梦"),

            SentenceData("我心里有一簇迎着烈日而生的花，比一切美酒都要芬芳滚烫的馨香，没过稻草人的胸膛，从此万寿无疆。", "费渡", "默读"),
            SentenceData("没有谁的生活会一直完美，但无论什么时候都要看着前方，满怀希望就会所向披靡。", "", "撒野"),
            SentenceData("希望我们都像对方一样勇敢。", "", "撒野"),
            SentenceData("我不是凝视深渊的人，我就是深渊。", "", "默读"),
            SentenceData("未经允许，擅自特别喜欢你，不好意思了。", "", "默读"),

            SentenceData("那么浅的胸口，那么深的心。", "", "默读"),
            SentenceData("满怀希望就会所向披靡。", "", "撒野"),
            SentenceData("一起去啊，去更远的地方。", "", "伪装学渣"),
            SentenceData("东楼贺朝，西楼谢俞。", "", "伪装学渣"),
            SentenceData("作为一个女人，你最好很出色，或者很漂亮。", "", "面包树上的女人"),

            SentenceData("当我真的痛苦万分、发出呻吟时，人人却说我是佯装痛苦，无病呻吟。", "太宰治", "斜阳"),
            SentenceData("我们坐在车上，经过的也许不过是几条熟悉的街衢，可是在漫天的火光中也有惊心动魄。", "张爱玲", "烬余录"),
            SentenceData("他还太年轻，没能意识到比起受惠者，施惠之人反而会有更强的图报心。", "毛姆", "人性的枷锁"),
            SentenceData("人是生而自由的，却又无时不处在枷锁之中。", "卢梭", "社会契约论"),
            SentenceData("我闯入自己的命运，如同跌进万丈深渊。", "茨威格", "一个陌生女人的来信"),

            SentenceData("如今他已经消失了，如同一滴水溶化在大海里了。", "卡尔维诺", "不存在的骑士"),
            SentenceData("年轻的时候我以为钱就是一切，现在老了才知道，确实如此。", "王尔德", ""),
            SentenceData("她那时候还太年轻，不知道所有命运赠送的礼物，都早已在暗中标好了价码 。", "茨威格", "断头王后"),

            SentenceData("走过危机四伏的成长，我们每个人都是青春的幸存者。", "史航", "房思琪的初恋花园·推荐语"),
            SentenceData("三十年前的月亮早已沉了下去，三十年前的人也死了，然而三十年前的故事还没完——完不了。", "张爱玲", "金锁记"),
        )
        val index = (0..arrayStr.lastIndex).random()
        return SentenceData(arrayStr[index].text, arrayStr[index].author, arrayStr[index].source)
    }
}