import React from 'react'

export default class Torrents extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            torrents: []
        }
    }

    componentDidMount() {
        fetch('/torrents')
        .then((res)=>res.json())
        .then((torrents)=>this.setState({ torrents }))
    }

    render() {
        return (
            <div>
                {this.state.torrents.map((torrent, i)=>(
                    <div
                        key={i}
                        style={{ border: 'solid 1px #ddd', marginBottom: 8, padding: 8 }}
                    >
                        <div>{Math.ceil(torrent.progress * 100)}%</div>
                        <div>
                            {torrent.files.map((file)=>(
                                <div key={file.name}>
                                    {file.name}
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        )
    }
}