import { Connection } from 'ssh-pool';

const connection = new Connection({
    remote: {
        user: process.env.SSH_USER,
        host: process.env.SSH_HOST,
    },
    key: process.env.SSH_KEY || undefined,
    stdout: process.stdout,
    stderr: process.stderr,
    log: (...args) => console.log(...args),
});
(async () => {
    await connection.copyToRemote(`${__dirname}/`, '/var/app', {
        ignores: ['.git', 'node_modules', 'download', 'package-lock.json'],
        rsync: '--delete',
    });
    await connection.run('ls -a /var/app');

    await connection.run('which node && node --version');
    await connection.run('which npm && npm --version');

    await connection.run('cd /var/app && npm install --production');

    await connection.run('cd /var/app && npx forever stopall');
    await connection.run('cd /var/app && npx forever start -c "npm start" bin/startServer.js');
    await connection.run('cd /var/app && npx forever list');
})().catch((error) => {
    console.log(error);
    process.exit(1);
});
