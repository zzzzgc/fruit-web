package com.fruit.web.base;

import com.fruit.web.controller.*;
import com.fruit.web.controller.login.LoginController;
import com.fruit.web.controller.order.OrderController;
import com.fruit.web.controller.person.AuthIdentityController;
import com.fruit.web.controller.person.BusinessInfoController;
import com.fruit.web.controller.person.PersonController;
import com.fruit.web.model.BusinessInfo;
import com.fruit.web.model._MappingKit;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxByMethodRegex;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.hikaricp.HikariCpPlugin;
import com.jfinal.template.Engine;

/**
 * API引导式配置
 */
public class JFConfig extends JFinalConfig {
    /**
     * 配置常量
     */
    @Override
    public void configConstant(Constants me) {
        PropKit.use("setting.properties");
        me.setDevMode(PropKit.getBoolean("devMode", false));
//		me.setFreeMarkerTemplateUpdateDelay(0);
//		me.setError404View("index.html");
    }

	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/", IndexController.class);
		me.add("/product", ProductController.class);
		me.add("/order", OrderController.class);
		me.add("/banner", BannerController.class);
		me.add("/classify", FruitTypeController.class);
		me.add("/cart", ProductCartController.class);
		me.add("/login", LoginController.class);
		me.add("/person", PersonController.class);
		me.add("authIdentity", AuthIdentityController.class);
		me.add("/businessInfo", BusinessInfoController.class);
	}

    @Override
    public void configEngine(Engine me) {
        //me.addSharedFunction("/common/_layout.html");
        //me.addSharedFunction("/common/_paginate.html");
    }

    /**
     * 配置插件
     */
    @Override
    public void configPlugin(Plugins me) {
        // 配置数据库连接池
        HikariCpPlugin dataSource = new HikariCpPlugin(PropKit.get("db.url"), PropKit.get("db.user"), PropKit.get("db.password"), PropKit.get("db.driver"));
        dataSource.setMaximumPoolSize(PropKit.getInt("db.poolMaxSize"));
        dataSource.setConnectionTimeout(PropKit.getInt("db.connectionTimeoutMillis"));
        me.add(dataSource);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dataSource);
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);

        me.add(arp);

        // 初始化应用缓存插件
        me.add(new EhCachePlugin());
    }

    /**
     * 配置全局拦截器
     */
    @Override
    public void configInterceptor(Interceptors me) {
        //me.add(new LoginInterceptor());
        //me.add(new AllowCrossDomain());
        me.add(new TxByMethodRegex("(.*save.*|.*update.*|.*create.*)"));
    }

    /**
     * 配置处理器
     */
    @Override
    public void configHandler(Handlers me) {
//		me.add(new PageHandler());
	}
}
