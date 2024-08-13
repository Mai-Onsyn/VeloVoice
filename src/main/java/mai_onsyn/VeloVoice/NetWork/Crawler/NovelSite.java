package mai_onsyn.VeloVoice.NetWork.Crawler;

import mai_onsyn.VeloVoice.Utils.Structure;

import java.util.List;

public interface NovelSite {

    Structure<List<String>> getContents();
}
