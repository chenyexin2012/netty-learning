## ChannelHandler

- handlerAdded: 当ChannelHandler添加至ChannelPipeline中时被调用

- handlerRemoved: 当ChannelHandler从ChannelPipeline中移除时被调用

- exceptionCaught: 当处理过程中在ChannelPipeline中出现异常时被调用

### ChannelInboundHandler

ChannelInboundHandler是ChannelHandler的一个子接口，用于处理入站消息。

- channelRegistered: 当Channel注册至EventLoop并且能够处理I/O事件时被调用

- channelUnregistered: 当Channel从EventLoop注销并且无法处理I/O事件时被调用

- channelActive: 当Channel处于活动状态时被调用，此时Channel已经连接/绑定并且已经就绪

- channelInactive: 当Channel离开活动状态并且不再连接它的远程节点时被调用

- channelRead: 当从Channel读取数据时被调用，用户重写此方法时，需要注意ByteBuf的释放与池化，否则可能出现内存泄露

- channelReadComplete: 当Channel的上一个读取操作完成时被调用

- userEventTriggered: 当用户事件触发时被调用

- channelWritabilityChanged: 当Channel的可写状态发生改变时被调用

### ChannelOutboundHandler

ChannelOutboundHandler是ChannelHandler的一个子接口，用于处理出站消息。

- bind: 当请求将Channel绑定到本地地址时被调用，常作用于服务端。

- connect: 当请求将Channel连接到远程节点时被调用，常作用于客户端。

- disconnect: 当请求将Channel从远程节点断开时被调用，常作用于客户端。

- close: 当请求将Channel关闭时被调用，常作用于服务端。

- deregister: 当请求将Channel从它的EvenLoop注销时被调用

- read: 当请求从Channel读取更多数据时被调用

- write: 当请求通过Channel将数据写到远程节点时被调用，调用此方法时需要考虑是否发生内存泄露(例如在处理write操作时丢弃了一个消息，此时需要手动释放它)

- flush: 当请求通过Channel将数据冲刷至远程节点时被调用
