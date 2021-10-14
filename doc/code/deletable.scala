type Deletable[L <: CList] <: CList = L match {
  case h &: t => (h | Deleted) &: Deletable[t]
  case CNil => CNil
}