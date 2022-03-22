package io.github.moehreag.axolotlclient.config;

public class AxolotlclientConfig {

    public nametagConf NametagConf;
    public static class nametagConf {
        public boolean showOwnNametag;

        public boolean useShadows;

        public nametagConf(boolean showOwnNametag, boolean useShadows) {
            this.showOwnNametag = showOwnNametag;
            this.useShadows = useShadows;
        }
    }


    public Badges badgeOptions;
    public static class Badges {
        public boolean showBadge;

        //public boolean showChatBadge = true;

        public boolean CustomBadge;


        public String badgeText;

        public Badges(boolean showBadge, boolean customBadge, String badgeText){
            this.showBadge=showBadge;
            this.CustomBadge=customBadge;
            this.badgeText=badgeText;
        }
    }

    public nh NickHider;
    public static class nh {
        public boolean hideNames;
        public String Name;

        public boolean hideOwnSkin;
        public boolean hideOtherSkins;

        public nh(boolean hideNames, String Name, boolean hideOwnSkin, boolean hideOtherSkins){
            this.hideNames = hideNames;
            this.Name = Name;
            this.hideOwnSkin = hideOwnSkin;
            this.hideOtherSkins = hideOtherSkins;
        }

    }

    public other General;
    public static class other {

        public other() {

        }
    }


    public rpcConfig RPCConfig;
    public static class rpcConfig {

        public boolean enableRPC = true;

        public boolean showActivity = true;

        public rpcConfig(boolean enableRPC, boolean showActivity){
            this.enableRPC = enableRPC;
            this.showActivity = showActivity;
        }
    }

    public AxolotlclientConfig(nametagConf nametagConf, Badges badges, nh nickHider, other other, rpcConfig rpcConfig){
        this.NametagConf = nametagConf;
        this.badgeOptions = badges;
        this.General = other;
        this.RPCConfig = rpcConfig;
        this.NickHider = nickHider;
    }

}
