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

## 知识点

### await和sync

netty本质上是异步的，但是可以通过await和sync来实现同步调用。以下列代码为例：

    public void connect() {
    
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.host, this.port)).await();
            if (future.isSuccess()) {
                this.channel = future.channel();
                log.info("success to connect");
                this.retries = 3;
            } else {
                log.info("failed to connect");
                if(this.retries-- > 0) {
                    connect();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
这段代码的目的是实现客户端连接失败重试，connect方法返回的是一个ChannelFuture对象，此处使用await方法来实现类似同步调用。
此处的调用链是DefaultChannelPromise.await()->DefaultPromise.await()，源码如下：
    
    public Promise<V> await() throws InterruptedException {
        if (isDone()) {
            return this;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        checkDeadLock();

        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    wait();
                } finally {
                    decWaiters();
                }
            }
        }
        return this;
    }


如何使用sync()来实现同步调用，当连接失败发生异常时，会重新抛出异常，而不会继续执行重连过程，DefaultPromise中sync方法源码如下：

    public Promise<V> sync() throws InterruptedException {
        await();
        rethrowIfFailed();
        return this;
    }

Netty声明，不要在IO线程中调用sync()、await()等相关阻塞的方法，这可能会带来死锁问题。可以使用addListener方法添加监听器这种异步的方式来解决。


























