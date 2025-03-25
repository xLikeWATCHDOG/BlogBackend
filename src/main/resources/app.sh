#!/bin/bash

# 获取当前目录下的所有 JAR 文件
select_jar() {
    echo "检测到以下 JAR 文件:"
    mapfile -t jars < <(ls *.jar 2>/dev/null)

    if [ ${#jars[@]} -eq 0 ]; then
        echo "未找到 JAR 文件，请确认后再试。"
        exit 1
    fi

    for i in "${!jars[@]}"; do
        echo "$((i+1)). ${jars[i]}"
    done

    # shellcheck disable=SC2162
    read -p "请选择要启动的 JAR 文件 (输入编号): " choice
    if [[ "$choice" =~ ^[0-9]+$ ]] && [ "$choice" -ge 1 ] && [ "$choice" -le ${#jars[@]} ]; then
        JAR_FILE="${jars[$((choice-1))]}"
    else
        echo "无效的选择。"
        exit 1
    fi
}

# 启动项目
start_app() {
    select_jar
    echo "正在启动项目: $JAR_FILE ..."
    java -jar "$JAR_FILE" --thin.debug=true &
    APP_PID=$!
    echo $APP_PID > app.pid
    echo "项目已启动，PID: $APP_PID"
}

# 停止项目
stop_app() {
    if [ -f app.pid ]; then
        APP_PID=$(cat app.pid)
        echo "正在停止项目 (PID: $APP_PID)..."
        kill $APP_PID
        rm -f app.pid
        echo "项目已停止。"
    else
        echo "未找到 PID 文件，应用可能未运行。"
    fi
}

# 重启项目
restart_app() {
    stop_app
    sleep 2
    start_app
}

# 处理命令
case "$1" in
    start)
        start_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        restart_app
        ;;
    *)
        echo "用法: $0 {start|stop|restart}"
        exit 1
        ;;
esac
