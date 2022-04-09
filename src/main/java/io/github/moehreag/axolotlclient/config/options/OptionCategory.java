package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class OptionCategory {

    Identifier Id;
    String name;
    private final List<Option> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();


    public OptionCategory(Identifier Id, String key){
        this.Id=Id;
        this.name=key;
    }

    public List<Option> getOptions(){return options;}

    public void add(Option option){options.add(option);}

    public void add(List<Option> options){this.options.addAll(options);}

    public void addSubCategory(OptionCategory category){subCategories.add(category);}

    public List<OptionCategory> getSubCategories(){return subCategories;}

    public void clearOptions(){options.clear();}

    public Identifier getID() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getTranslatedName(){return I18n.translate(name);}

}