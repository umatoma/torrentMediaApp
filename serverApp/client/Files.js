import React from 'react'

export default class Files extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            directories: [],
            files: []
        }
    }

    componentDidMount() {
        this.fetchFiles()
    }

    fetchFiles() {
        fetch(this.getFilesUrl())
        .then((res) => res.json())
        .then((files) => this.setState({ files }))
    }

    getFilesUrl() {
        return '/' + [
            'files',
            ...this.state.directories,
        ].map((dir) => encodeURIComponent(dir)).join('/')
    }

    getDownloadFileUrl(file) {
        return '/' + [
            'file',
            'download',
            ...this.state.directories,
            file.name,
        ].map((dir) => encodeURIComponent(dir)).join('/')
    }

    pushDirectory(directory) {
        const directories = this.state.directories.concat(directory.name)
        this.setState({ directories }, () => {
            this.fetchFiles()
        })
    }

    render() {
        const directoryFiles = this.state.files.filter((file) => file.type === 'directory')
        const files = this.state.files.filter((file) => file.type === 'file')

        return (
            <div>
                <nav className="breadcrumb has-arrow-separator" aria-label="breadcrumbs">
                    <ul>
                        <li>
                            <a href="#">/</a>
                        </li>
                        {this.state.directories.map((directory, i) => (
                            <li key={i}>
                                <a href="#">{directory}</a>
                            </li>
                        ))}
                    </ul>
                </nav>

                {directoryFiles.map((directoryFile, i) => (
                    <div
                        key={i}
                        className="button is-fullwidth"
                        style={{ marginBottom: 8, justifyContent: 'left', overflowX: 'hidden' }}
                        onClick={() => this.pushDirectory(directoryFile)}
                    >
                        <span
                            className="tag is-primary"
                            style={{ marginRight: 8 }}
                        >
                            {directoryFile.type}
                        </span>
                        {directoryFile.name}
                    </div>
                ))}
                {files.map((file, i) => (
                    <a
                        key={i}
                        className="button is-fullwidth"
                        style={{ marginBottom: 8, justifyContent: 'left', overflowX: 'hidden' }}
                        href={this.getDownloadFileUrl(file)}
                        target="_blank"
                    >
                        <span
                            className="tag is-primary"
                            style={{ marginRight: 8 }}
                        >
                            {file.type}
                        </span>
                        {file.name}
                    </a>
                ))}
            </div>
        )
    }
}