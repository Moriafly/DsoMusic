package com.dirror.music.foyou.sentence

import android.util.Log
import com.dirror.music.util.isChinese

/**
 * 优化来自一言的句子单例
 */
object OptimizeHitokoto {
    fun optimizeHitokoto(sentence: SentenceData): SentenceData {
        Log.e("句子", "【${sentence.text}】【${sentence.author}】【${sentence.source}】")

        var newSentence = sentence
        newSentence = sentenceShield(newSentence) // 屏蔽
        var text = newSentence.text
        text = sentenceTextRepair(text) // 句子标点修复
        text = sentenceTextNewLine(text) // 标点后换行，然后去掉最后一个换行

        var author = newSentence.author
        author = sentenceAuthorRepair(author)

        var source = newSentence.source
        source = sentenceSourceRepair(source)

        newSentence = SentenceData(text, author, source)
        return newSentence
    }

    private fun sentenceShield(sentence: SentenceData): SentenceData {
        return when (sentence.text) {
            "金钱和女人，是人生犯错的根源。" -> SentenceData("梦想只能控制你自己的大脑，但是有了钱，你就能控制别人的大脑。", "维卡斯·施瓦卢普", "贫民窟的百万富翁")
            "「あたいってば最強ね！」（本小姐最強）" -> SentenceData("本小姐最强！", sentence.author, sentence.source)
            "XX什么的，最讨厌了！" -> SentenceData("为你。", "Foyou", "")
            "用番茄Lu过…比香蕉好←那是你没领悟到香蕉的正确用法" -> SentenceData("Hello!", "Foyou", "")
            "所爱隔山海 山海亦可平" -> SentenceData("所爱隔山海，山海亦可平。", "", "")
            "我还在原地等你 你却已经忘记来过这里" -> SentenceData("我还在原地等你，你却已经忘记来过这里。", "", "")
            "人生四然：来是偶然，去是必然，尽其当然，顺其自然 ..." -> SentenceData("人生四然：来是偶然，去是必然，尽其当然，顺其自然。", "", "san.ci")
            "我裤子都脱了你就给我看这个！！！" -> SentenceData("我裤子都脱了你就给我看这个！", "", "2ch")
            "“沙漠，扬起你的沙砾，遮蔽太阳的光芒吧！！”" -> SentenceData("沙漠，扬起你的沙砾，遮蔽太阳的光芒吧！", "无疤者奥斯里安", "魔兽世界")
            "别师犹染凡心 剑端新雪霁 独恨无留迹" -> SentenceData("别师犹染凡心，剑端新雪霁，独恨无留迹。", "", "魔道祖师")
            "你是否会等待着我?我还想变的更加完美..." -> SentenceData("你是否会等待着我？我还想变的更加完美……", "", "LoveLive! Sunshine!")
            "什么都无法舍弃的人，什么都无法改变 。" -> SentenceData("什么都无法舍弃的人，什么都无法改变。", "", "进击的巨人")
            "如果我们就这样死在山里面 他们会不会说我们是殉情" -> SentenceData("如果我们就这样死在山里面，他们会不会说我们是殉情？", "", "")
            "H什么的最讨厌了！" -> SentenceData("晚安。", "Foyou", "")
            " 像旧时候，像老朋友。" -> SentenceData("像旧时候，像老朋友。", "", "")
            "说时依旧,有泪如倾" -> SentenceData("说时依旧，有泪如倾。", "三毛", "说时依旧")
            "到底要被救多少次，才会甘心啊！？" -> SentenceData("到底要被救多少次，才会甘心啊？", "贝尔", "在地下城寻找邂逅是否搞错了什么")
            "盈盈一水间,脉脉不得语." -> SentenceData("盈盈一水间，脉脉不得语。", "", "迢迢牵牛星")
            "有时候，和你吵架，我宁愿当一个输家，也不愿意赢我爱的你！！！" -> SentenceData("有时候，和你吵架，我宁愿当一个输家，也不愿意赢我爱的你！", "", "Az殇芯")
            "生而贫穷并无过错,死而贫穷才是遗憾." -> SentenceData("生而贫穷并无过错，死而贫穷才是遗憾。", "", "财箴")
            "时间可以治愈？如果时间也病了怎么办" -> SentenceData("时间可以治愈？如果时间也病了怎么办？", "古手梨花", "寒蝉明泣之时")
            "只要你想 你就是在天涯海角 我都会追去 。" -> SentenceData("只要你想，你就是在天涯海角，我都会追去。", "", "")
            "醉后不知天在水 满船清梦压星河" -> SentenceData("醉后不知天在水，满船清梦压星河。", "唐温如", "题龙阳县青草湖")
            "你最可爱,我说时来不及思索,但思索过后,还是这样说" -> SentenceData("你最可爱，我说时来不及思索，但思索过后，还是这样说。", "", "普希金")
            "若教眼底无离恨，不信人间有白头" -> SentenceData("若教眼底无离恨，不信人间有白头。", "辛弃疾", "鹧鸪天·晚日寒鸦一片愁")
            "难受就是难受…不要让开心变成一种负担…" -> SentenceData("难受就是难受，不要让开心变成一种负担。", "", "")
            "掌覆道左阴翳 妄握天格命理" -> SentenceData("掌覆道左阴翳，妄握天格命理。", "", "魔道祖师")
            "被一个自己不感兴趣的人示好，你不觉得没有比这更恶心的事吗" -> SentenceData("被一个自己不感兴趣的人示好，你不觉得没有比这更恶心的事吗？", "", "人渣的本愿")
            "以为是初见，其实是重逢。\\r\\n" -> SentenceData("以为是初见，其实是重逢。", "木苏里", "全球高考")
            "你的败因只有一个，就是与我为敌。 -" -> SentenceData("你的败因只有一个，就是与我为敌。", "", "家庭教师")
            "厚道的人 运气都不会太差" -> SentenceData("厚道的人，运气都不会太差。", "雷军", "")
            "爱,其实很简单，困难的是去接受它。" -> SentenceData("爱，其实很简单，困难的是去接受它。", "", "通灵王")
            "你还是...笑起来...最棒了" -> SentenceData("你还是……笑起来……最棒了。", "", "最终幻想XIV")
            "人而无仪，不死何为!" -> SentenceData("人而无仪，不死何为！", "", "相鼠")
            "我愿披挂长风扬鞭策马 去看去唱那四海之大" -> SentenceData("我愿披挂长风扬鞭策马，去看去唱那四海之大。", "", "最大化+")
            "斜晖脉脉水悠悠,肠断白频洲." -> SentenceData("斜晖脉脉水悠悠，肠断白频洲。", "温庭筠", "望江南")
            "衣带渐宽终不悔,为伊消得人憔悴." -> SentenceData("衣带渐宽终不悔，为伊消得人憔悴。", "柳永", "蝶恋花")
            "为什么你宁愿吃生活上的苦 而不愿意吃学习上的苦？" -> SentenceData("为什么你宁愿吃生活上的苦，而不愿意吃学习上的苦？", "", "")
            "民谣有三：爱情 理想 远方   听者有三：孤独 平庸 落魄" -> SentenceData("民谣有三：爱情、理想、远方。听者有三：孤独、平庸、落魄。", "", "殿下")
            "愿字起于心头 转瞬又死于心间" -> SentenceData("愿字起于心头，转瞬死于心间。", "", "")
            "想和你重新认识一次 从你叫什么名字说起。" -> SentenceData("想和你重新认识一次，从你叫什么名字说起。", "", "你的名字")
            "正因为不会发生，人们才称它为”奇迹“" -> SentenceData("正因为不会发生，人们才称它为奇迹。", "", "KANON")
            "为了你,如果是为了你,即使我被困在永远的迷宫中,也没关系." -> SentenceData("为了你，如果是为了你，即使我被困在永远的迷宫中，也没关系。", "", "魔法少女小圆")
            "妆罢低声问夫婿，画眉深浅入时无。" -> SentenceData("妆罢低声问夫婿，画眉深浅入时无。", "朱庆馀", "近试上张籍水部")
            "如果当初握住的不是硬币，而是勇者的手......" -> SentenceData("如果当初握住的不是硬币，而是勇者的手……", "", "中二病也要谈恋爱！恋")
            else -> sentence
        }
    }

    // 为某些没加结束标点符号的句子加上标点符号
    private fun sentenceTextRepair(string: String): String {
        return if (string.isNotEmpty()) {
            if (string.last().isChinese()) {
                "${string}。"
            } else {
                string
            }
        } else {
            string
        }
    }

    fun sentenceTextNewLine(string: String): String {
        var sentenceTextStr = string
        // 符号修复
        sentenceTextStr = sentenceTextStr.replace("“","")
        sentenceTextStr = sentenceTextStr.replace("”","")
        sentenceTextStr = sentenceTextStr.replace("\"","")
        sentenceTextStr = sentenceTextStr.replace("......","……")
        sentenceTextStr = sentenceTextStr.replace("\\","")

        // 中文符号
        sentenceTextStr = sentenceTextStr.replace("，","，\n")
        sentenceTextStr = sentenceTextStr.replace("。","。\n")
        sentenceTextStr = sentenceTextStr.replace("；","；\n")
        sentenceTextStr = sentenceTextStr.replace("：","：\n")
        sentenceTextStr = sentenceTextStr.replace("？","？\n")
        sentenceTextStr = sentenceTextStr.replace("！","！\n")

        // 英文符号
        sentenceTextStr = sentenceTextStr.replace(",",",\n")
        sentenceTextStr = sentenceTextStr.replace(".",".\n")
        sentenceTextStr = sentenceTextStr.replace("?","?\n")

        // 除去最后一个换行
        if (sentenceTextStr.endsWith('\n')) {
            // 截取字符串到 n-1
            sentenceTextStr = sentenceTextStr.substring(0, sentenceTextStr.length - 1)
        }

        return sentenceTextStr
    }

    private fun sentenceAuthorRepair(string: String): String {
        var sentenceAuthorStr = string
        sentenceAuthorStr = when (sentenceAuthorStr) {
            "上软" -> "上海软星"
            "网络", "网友", "ul", "null", "此账号已注销" -> ""
            else -> sentenceAuthorStr
        }
        return sentenceAuthorStr
    }

    private fun sentenceSourceRepair(string: String): String {
        var sentenceSourceStr = string
        sentenceSourceStr = when (sentenceSourceStr) {
            "仙剑4", "仙剑奇侠传4" -> "仙剑奇侠传四"
            "剑网3" -> "剑侠情缘三网络版"
            "崩坏3" -> "崩坏三"
            "三体:死神永生" -> "三体：死神永生"
            "小米公司", "XiaoMi" -> "小米"
            "网络", "原创", "自编", "抖音", "哔哩哔哩", "网易云", "网易热评", "内涵段子TV", "酷狗音乐", "知乎",
                "b站评论", "其他", "网络；忘记在哪看到的了", "知乎用户尚守晨", "617969448", "Qihoo360",
                "网易云音乐热评", "bilibili", "某电视剧", "自己"
                -> ""
            "来自网络", "网络热语", "热评", "B站弹幕" -> ""
            "qq空间", "QQ空间", "互联网", "微博" -> ""
            "——顾城" -> "顾城"
            "《易经》" -> "易经"
            "8848太监手机" -> "8848"
            else -> sentenceSourceStr
        }
        return sentenceSourceStr
    }
}