import { env } from './config/env';
import { createApp } from './app';

const app = createApp();

app.listen(env.PORT, () => {
  console.log('=============================================================');
  console.log(`🚀 KayanSoft Secure Server (TypeScript + Prisma) running on port ${env.PORT}`);
  console.log('🔒 HMAC Validation Active with Master Secret Protection');
  console.log('=============================================================');
});
