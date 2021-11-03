#!/usr/bin/env bash

# settings-gio-sdk.xml
# 需要配置 maven 中央仓库的账号
# 用户名 dev-growing
# 密码 联系 wangtong@growingio.com, aiyanbo@growingio.com
# m1 上增加-Papple-silicon执行相关命令

mvn -s ~/.m2/settings-gio-sdk.xml clean install deploy -Pcdp -Dmaven.test.skip=true -Dgpg.skip=true
