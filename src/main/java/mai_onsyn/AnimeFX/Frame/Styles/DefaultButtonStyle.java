package mai_onsyn.AnimeFX.Frame.Styles;

import mai_onsyn.AnimeFX.Frame.Module.DiffusionButton;

public class DefaultButtonStyle implements ButtonStyle{
    @Override
    public DiffusionButton createButton(String name) {
        return new DiffusionButton()
                .name(name)
                .height(30)
                .init();
    }
}
