## Dev setup

To start dependencies for dev cluster run:

`helmfile --file k8/dev/helmfile.yaml apply`

The following binaries are required:
`helm`, `helmfile` and helm-diff plugin `helm plugin install https://github.com/databus23/helm-diff`
