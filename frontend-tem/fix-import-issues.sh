#!/bin/bash

echo "🔧 开始修复动态导入问题..."

# 1. 停止可能运行的开发服务器
echo "1. 停止开发服务器..."
killall node 2>/dev/null || true

# 2. 清除所有缓存
echo "2. 清除缓存..."
rm -rf node_modules/.cache
rm -rf node_modules/.vite
rm -rf dist
rm -rf .vite

# 3. 清除浏览器缓存相关
echo "3. 清除 localStorage 和其他缓存..."
# 这个需要在浏览器中手动执行或通过脚本

# 4. 重新安装依赖
echo "4. 重新安装依赖..."
npm run clean
npm run i

# 5. 重新启动开发服务器
echo "5. 重新启动开发服务器..."
echo "请手动运行: npm run dev"

echo "✅ 修复脚本执行完成！"
echo "📝 如果问题仍然存在，请："
echo "   1. 手动清除浏览器缓存"
echo "   2. 使用无痕浏览模式测试"
echo "   3. 检查网络连接"