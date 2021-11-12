# usage: 
# . initshell.sh dev01
# . initshell.sh prd01
namespace="onlex-sinnet-$1"
alias kc="kubectl"
alias k="kc --namespace $namespace"
alias h="helm --namespace $namespace"