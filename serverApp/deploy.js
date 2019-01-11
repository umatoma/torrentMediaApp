import { Connection } from 'ssh-pool'

const connection = new Connection({
    remote: {
        user: process.env.SSH_USER,
        host: process.env.SSH_HOST,
    },
    key: process.env.SSH_KEY || undefined,
    stdout: process.stdout,
    stderr: process.stderr,
});
(async () => {
    await connection.run('cd /var/app && npx pm2 delete all')
        .catch(() => console.log('Failed to stop the application'))

    await connection.copyToRemote(`${__dirname}/`, '/var/app', {
        ignores: ['.git', 'node_modules', 'download', 'package-lock.json'],
        rsync: '--delete',
    })
    await connection.run('ls -a /var/app')

    await connection.run('which node && node --version')
    await connection.run('which npm && npm --version')

    await connection.run('cd /var/app && npm install')
    await connection.run('cd /var/app && make build')

    await connection.run(
        'cd /var/app && ' +
        `BASIC_USER=${process.env.BASIC_USER} BASIC_PASS=${process.env.BASIC_PASS} npx pm2 start npm --name app -- start`
    )
    await connection.run('cd /var/app && npx pm2 list')
})().catch((error) => {
    console.log(error)
    process.exit(1)
})
